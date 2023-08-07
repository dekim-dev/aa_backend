package dekim.aa_backend.service;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.exception.EmailAlreadyExistsException;
import dekim.aa_backend.exception.NicknameAlreadyExistsException;
import dekim.aa_backend.persistence.UserRepository;
import dekim.aa_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserDetailService userDetailService;
  private final TokenProvider tokenProvider;

  public UserResponseDTO create(UserRequestDTO dto) {
    // 비어있는 필드 처리
    if (dto == null || dto.getEmail() == null || dto.getNickname() == null || dto.getPassword() == null) {
      throw new RuntimeException("Invalid arguments");
    }

    // 이메일, 닉네임 중복 검사
    checkDuplicateEmailAndNickname(dto.getEmail(), dto.getNickname());

    // User(Entity)로 만들어서 저장
    User newUser = userRepository.save(User.builder()
            .email(dto.getEmail())
            .password(bCryptPasswordEncoder.encode(dto.getPassword()))
            .nickname(dto.getNickname())
            .build());

    // 저장된 값을 UserResponseDTO에 넣어서 반환
    return UserResponseDTO.builder()
            .email(newUser.getEmail())
            .nickname(newUser.getNickname())
            .role(newUser.getRole())
            .build();
  }

  // 이메일, 닉네임 중복검사 메서드
  private void checkDuplicateEmailAndNickname(String email, String nickname) {
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException(email);
    }
    if (userRepository.existsByNickname(nickname)) {
      throw new NicknameAlreadyExistsException(nickname);
    }
  }

  // 로그인
  public UserResponseDTO login(UserRequestDTO dto) {
    // Email로 존재하는 회원 찾기
    User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

    // 비밀번호가 match 하지 않을 경우 예외처리. 보안을 위해 이메일과 비밀번호 중 어느 필드가 문제인지 언급 X
    if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid email or password");
    }

    // 토큰 생성
    String token = tokenProvider.generateToken(dto, Duration.ofDays(14));

    // CustomUserDetails 객체 생성 (User Entity를 직접 사용하지 않기 위해 생성한 클래스)
    CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(dto.getEmail());

    // 인증 정보 설정
    Authentication authentication = new UsernamePasswordAuthenticationToken(dto, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder에 저장
    log.info(dto.getEmail()+ "의 token🔐 " + token);

    // UserResponsDTO로 반환
    return UserResponseDTO.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .role(user.getRole())
            .token(token)
            .build();
  }
}
