  package dekim.aa_backend.config.jwt;

  import com.fasterxml.jackson.databind.ObjectMapper;
  import dekim.aa_backend.dto.GlobalResponseDTO;
  import dekim.aa_backend.dto.UserRequestDTO;
  import dekim.aa_backend.security.CustomUserDetails;
  import jakarta.servlet.FilterChain;
  import jakarta.servlet.ServletException;
  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;
  import lombok.RequiredArgsConstructor;
  import org.springframework.http.HttpStatus;
  import org.springframework.security.core.Authentication;
  import org.springframework.security.core.context.SecurityContextHolder;
  import org.springframework.stereotype.Component;
  import org.springframework.util.StringUtils;
  import org.springframework.web.filter.OncePerRequestFilter;

  import java.io.IOException;
  @Component
  @RequiredArgsConstructor
  public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      String authorizationHeader = request.getHeader("Authorization");

      if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        if (tokenProvider.validToken(token)) {
          Authentication authentication = tokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (tokenProvider.refreshTokenValidation(token)) {
          // 액세스 토큰이 만료되었을 경우, 새로운 액세스 토큰 발급받아서 설정
          refreshAccessToken(response);
        }
      }
      filterChain.doFilter(request, response);
    }

    private void refreshAccessToken(HttpServletResponse response) throws IOException {
      // 현재 사용자 정보 가져오기
      CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String userEmail = userDetails.getUser().getEmail();

      // 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
      UserRequestDTO userRequestDTO = UserRequestDTO.builder()
              .email(userEmail)
              .build();

      String newAccessToken = tokenProvider.createToken(userRequestDTO, "ACCESS");
      response.setHeader("Authorization", "Bearer " + newAccessToken);

      Authentication authentication = tokenProvider.getAuthentication(newAccessToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
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
