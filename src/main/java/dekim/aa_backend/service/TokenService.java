package dekim.aa_backend.service;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.RefreshTokenRepository;
import dekim.aa_backend.persistence.UserRepository;
import dekim.aa_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TokenService {
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public String createNewAccessToken(String refreshToken) {
    // 리프레시 토큰 유효성 검증
    if (!tokenProvider.refreshTokenValidation(refreshToken)) {
      return "리프레시토큰 유효하지 않음";
    }

    String userEmail = refreshTokenRepository.findByUserEmail(refreshToken).toString();
    Optional<User> optionalUser = userRepository.findByEmail(userEmail);
    CustomUserDetails userDetails = optionalUser.map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    log.info("user" + optionalUser);

    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

    UserDetails springUserDetails = org.springframework.security.core.userdetails.User.withUsername(userDetails.getUsername())
            .password(userDetails.getPassword())
            .authorities(authorities)
            .build();

    Authentication authentication = new UsernamePasswordAuthenticationToken(springUserDetails, "", authorities);
    return tokenProvider.createAToken(authentication, "ACCESS");
  }

}
