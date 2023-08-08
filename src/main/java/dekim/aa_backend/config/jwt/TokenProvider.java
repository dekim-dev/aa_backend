package dekim.aa_backend.config.jwt;

import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.entity.RefreshToken;
import dekim.aa_backend.persistence.RefreshTokenRepository;
import dekim.aa_backend.service.UserDetailService;
import dekim.aa_backend.service.UserService;
import io.jsonwebtoken.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {
  private final JwtProperties jwtProperties;
  private final UserService userService;
  private final UserDetailService userDetailService;
  private final RefreshTokenRepository refreshTokenRepository;

  private static final long ACCESS_TIME = 60 * 1000L;
  private static final long REFRESH_TIME = 2 * 60 * 1000L;
  public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
  public static final String REFRESH_TOKEN = "REFRESH_TOKEN";


//  public String generateToken(UserRequestDTO user, Duration expiredAt) {
//    Date now = new Date();
//    return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
//  }

  // header 토큰을 가져오는 기능
  public String getHeaderToken(HttpServletRequest request, String type) {
    return type.equals("ACCESS") ? request.getHeader(ACCESS_TOKEN) : request.getHeader(REFRESH_TOKEN);
  }
  // 토큰 생성
  public TokenDTO makeTokens(UserRequestDTO user) {
    return new TokenDTO(createToken(user, "ACCESS"), createToken(user, "REFRESH"));
  }
  public String createToken(UserRequestDTO user, String type) {
    Date now = new Date();
    long time = type.equals("ACEESS") ? ACCESS_TIME : REFRESH_TIME;

    return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.getIssuer())
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + time))
            .setSubject(user.getEmail())
//            .claim("email", user.getEmail())
            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
            .compact();
  }

  public boolean validToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }
    return false;
  }

  // refreshToken 검증
  public Boolean refreshTokenValidation(String token) {
    if(!validToken(token)) return false;
    Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserEmail(getEmailFromToken(token));

    return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken());
  }

  // 인증 객체 생성
  public Authentication createAuthentication(String email) {
    UserDetails userDetails = userDetailService.loadUserByUsername(email);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }
//    public Authentication getAuthentication(String token) {
//      Claims claims = getClaims(token);
//      Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
//      return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
//    }

  // 토큰에서 email 가져오는 기능
  public String getEmailFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build().parseClaimsJws(token).getBody().getSubject();
  }

//    public String getUserId(String token) {
//      Claims claims = getClaims(token);
//      return claims.get("email", String.class);
//    }


  // ACCESS TOKEN 헤더 설정
  public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
    response.setHeader("ACCESS_TOKEN", accessToken);
  }

  // ACCESS TOKEN 헤더 설정
  public void setRefreshToken(HttpServletResponse response, String refreshToken) {
    response.setHeader("REFRESH_TOKEN", refreshToken);
  }

//
//    private Claims getClaims(String token) {
//    return Jwts.parser()
//            .setSigningKey(jwtProperties.getSecretKey())
//            .parseClaimsJws(token)
//            .getBody();
//    }
  }

