package dekim.aa_backend.service;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.constant.Authority;
import dekim.aa_backend.constant.IsActive;
import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.TokenRequestDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.entity.RefreshToken;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.RefreshTokenRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailService emailService;

    @Transactional
    public UserResponseDTO signup(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
        String authKey = emailService.createKey();
        userRequestDTO.setAuthKey(authKey);

        String emailContent = "안녕하세요. <br /><br />Appropriate Attention 회원가입을 완료하기 위해<br /> 아래 링크를 클릭해 주세요. <br /><br />";
        emailContent += "<a href=\"http://localhost:8111/auth/email_auth?email=" + userRequestDTO.getEmail() + "&authKey=" + userRequestDTO.getAuthKey() + "\">인증하기</a>";
        emailService.sendEmailWithLink(userRequestDTO.getEmail(), "[Appropriate Attention] 회원가입 이메일 인증", emailContent);
        User user = userRequestDTO.toUser(passwordEncoder);
        return UserResponseDTO.of(userRepository.save(user));
    }

    @Transactional
    public TokenDTO login(UserRequestDTO userRequestDTO) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = userRequestDTO.toAuthentication();

        // 2. 계정 존재 여부 확인
        User loginUser = userRepository.findByEmail(userRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 계정입니다."));

        // 3. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(userRequestDTO.getPassword(), loginUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다.");
        }

        // 4. 사용자의 권한 및 활성화 상태 확인
        if (loginUser.getAuthority() != Authority.ROLE_ADMIN && loginUser.getAuthority() != Authority.ROLE_USER) {
            throw new IllegalArgumentException("권한이 올바르지 않습니다.");
        }

        // 5. ROLE_USER인 경우에만 isActive 확인
        if (loginUser.getAuthority() == Authority.ROLE_USER) {
            if (loginUser.getIsActive() != IsActive.ACTIVE) {
                throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
            }
        }

        // 6. 인증 및 토큰 생성
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

            // 7. RefreshToken 저장
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(tokenDTO.getRefreshToken())
                    .expiresIn(tokenDTO.getRefreshTokenExpiresIn())
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            return tokenDTO;
        } catch (AuthenticationException e) {
            throw e;
        }
    }


    @Transactional
    public TokenDTO reissue(TokenRequestDTO tokenRequestDTO) {

        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDTO.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDTO.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateAccessToken(authentication);

        // 토큰 발급
        return tokenDTO;
    }

    @Transactional
    public void logout(String refreshToken) {

        int deletedCount = refreshTokenRepository.deleteByValue(refreshToken);
        log.info("👉🏻refreshToken: " + refreshToken);

        if (deletedCount == 0) {
            throw new RuntimeException("리프레시 토큰 삭제에 실패했습니다.");
        }
        SecurityContextHolder.clearContext();
    }

    // 닉네임 중복 확인
    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 이메일 중복 확인
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원가입 - 이메일 인증 (인증키 확인)
    public void checkEmailWithAuthKey(String email, String authKey) throws IllegalArgumentException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (authKey.equals(user.getAuthKey())) {
                user.setIsActive(IsActive.ACTIVE);
                user.setAuthKey("");
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("인증키가 올바르지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("이메일 주소를 찾을 수 없습니다.: " + email);
        }
    }

    // 비밀번호 재발급 & 비밀번호 업데이트
    public void updatePasswordWithAuthKey(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found."));
        String tempPw = emailService.sendPasswordAuthKey(email);

        // 임시 비밀번호로 업데이트
        String encodedPassword = passwordEncoder.encode(tempPw);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
