package dekim.aa_backend.service;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.exception.EmailAlreadyExistsException;
import dekim.aa_backend.exception.NicknameAlreadyExistsException;
import dekim.aa_backend.persistence.RefreshTokenRepository;
import dekim.aa_backend.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserDetailService userDetailService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

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
            .orElseThrow(() -> new IllegalArgumentException("Invalid email"));
    // 비밀번호가 match 하지 않을 경우 예외처리. 보안을 위해 이메일과 비밀번호 중 어느 필드가 문제인지 언급 X ✨보류 ㅎ
    if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid password");
    }

      // 토큰 생성
      TokenDTO tokenDTO = tokenProvider.makeTokens(dto);
      //리프레시 토큰 확인 후 생성 / 업데이트
      tokenProvider.saveOrUpdateRefreshToken(dto.getEmail(), tokenDTO.getRefreshToken());

      log.info(dto.getEmail() + "의 ACEESS_token🔐 " + tokenDTO.getAccessToken());
      log.info(dto.getEmail() + "의 REFRESH_token🔐 " + tokenDTO.getRefreshToken());

    // UserResponsDTO로 반환
    return UserResponseDTO.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .role(user.getRole())
            .accessToken(tokenDTO.getAccessToken())
            .build();
  }
}
