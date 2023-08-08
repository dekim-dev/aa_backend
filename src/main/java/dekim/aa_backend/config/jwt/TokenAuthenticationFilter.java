package dekim.aa_backend.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.GlobalResponseDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

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
          // 헤더에 액세스 토큰 추가
          tokenProvider.setHeaderAccessToken(response, newAccessToken);
          // 인증 객체 설정
          setAuthentication(tokenProvider.getEmailFromToken(newAccessToken));
        }
        else {
          jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
          return;
        }
      }
    }
      filterChain.doFilter(request, response);
    }

    public void setAuthentication (String email){
      Authentication authentication = tokenProvider.createAuthentication(email);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status)  {
      response.setStatus(status.value());
      response.setContentType("application/json");
      try {
        String json = new ObjectMapper().writeValueAsString(GlobalResponseDTO.of(msg, status.value()));
        response.getWriter().write(json);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
}
