package dekim.aa_backend.controller;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.security.CustomUserDetails;
import dekim.aa_backend.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.entity.RefreshToken;
import dekim.aa_backend.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api")
public class TokenController {

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private UserDetailService userDetailService;

  @Autowired
  private TokenService tokenService;

  @PostMapping("/refresh-token")
  public ResponseEntity<String> refreshAccessToken(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RefreshToken tokenDTO) {
    try {
      // 리프레시 토큰 유효성 검증
      if (!tokenProvider.refreshTokenValidation(tokenDTO.getRefreshToken())) {
        return ResponseEntity.badRequest().body("Invalid refresh token");
      }

      // 리프레시 토큰에서 사용자 정보 추출
      String userEmail = userDetails.getUser().getEmail();
      log.info("1. userEmail: " + userEmail);

      // 사용자 정보로부터 새로운 액세스 토큰 발급
//      String userRole = userDetails.getAuthorities().stream()
//              .map(GrantedAuthority::getAuthority)
//              .findFirst()
//              .orElse("ROLE_USER"); // 기본값 설정

      UserRequestDTO userRequestDTO = UserRequestDTO.builder()
              .email(userEmail)
//              .role(Role.valueOf(userRole))
              .build();

      String newAccessToken = tokenProvider.createToken(userRequestDTO, "ACCESS");
      log.info("2. newAccessToken: " + newAccessToken);

      // 새로운 액세스 토큰을 응답으로 전송
      return ResponseEntity.ok(newAccessToken);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing access token");
    }
  }
}
