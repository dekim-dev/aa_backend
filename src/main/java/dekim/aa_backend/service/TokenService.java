//package dekim.aa_backend.service;
//
//import dekim.aa_backend.config.jwt.TokenProvider;
//import dekim.aa_backend.dto.UserRequestDTO;
//import dekim.aa_backend.entity.RefreshToken;
//import dekim.aa_backend.entity.User;
//import dekim.aa_backend.security.CustomUserDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@RequiredArgsConstructor
//@Service
//public class TokenService {
//  private final TokenProvider tokenProvider;
//  private final RefreshTokenService refreshTokenService;
//  private final UserService userService;
//
//  public String createNewAccessToken(String refreshToken) {
////    // 토큰 유효성 검사에 실패하면 예외 발생
////    if(!tokenProvider.validToken(refreshToken)) {
////      throw new IllegalArgumentException("Unexpected token");
////    }
//    Long userNo = refreshTokenService.findByRefreshToken(refreshToken).getUserNo();
//    User user = userService.findById(userNo);
//    UserRequestDTO userRequestDTO = new UserRequestDTO();
//    userRequestDTO.setEmail(user.getEmail());
//    userRequestDTO.setPassword(user.getPassword()); // 암호화된 패스워드 사용
//    return tokenProvider.generateToken(userRequestDTO, Duration.ofHours(2));
//  }
//}
