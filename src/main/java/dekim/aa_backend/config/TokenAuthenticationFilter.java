package dekim.aa_backend.config;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final TokenProvider tokenProvider;
  private final UserRepository userRepository;
  private final static String HEADER_AUTHORIZATION = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // 요청 헤더의 Authorization 키의 값 조회
//    String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
    // 가져온 값에서 접두사 제거
    String accessToken = tokenProvider.getHeaderToken(request, "ACCESS");
    String refreshToken = tokenProvider.getHeaderToken(request, "REFRESH");
    // 가져온 토큰이 유용한지 확인하고, 유효한 때는 인증 정보를 설정
    if (accessToken != null) {
      if (tokenProvider.validToken(accessToken)) {
        setAuthentication(tokenProvider.getEmailFromToken(accessToken));
      } else if (refreshToken != null) {
        boolean isRefreshToken = tokenProvider.refreshTokenValidation(refreshToken);
        if (isRefreshToken) {
          String userEmail = tokenProvider.getEmailFromToken(refreshToken);

          // 로그인한 이메일 정보를 활용하여 UserRequestDTO 객체 생성
          UserRequestDTO dto = new UserRequestDTO();
          dto.setEmail(userEmail);

          // 새로운 액세스 토큰 생성
          String newAccessToken = tokenProvider.createToken(dto, "ACCESS");
          tokenProvider.setHeaderAccessToken(response, newAccessToken);
          setAuthentication(tokenProvider.getEmailFromToken(newAccessToken));
        }
//      Authentication authentication = tokenProvider.getAuthentication(token);
//      SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }
      filterChain.doFilter(request, response);
    }

    public void setAuthentication (String email){
      Authentication authentication = tokenProvider.createAuthentication(email);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//  private String getAccessToken(String authorizationHeader) {
//    if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
//      return authorizationHeader.substring(TOKEN_PREFIX.length());
//    }
//    return null;
//  }

}
