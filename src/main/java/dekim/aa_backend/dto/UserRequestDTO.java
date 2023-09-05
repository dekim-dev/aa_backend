package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Authority;
import dekim.aa_backend.entity.User;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

  private String email;
  private String password;
  private String nickname;

  public User toUser(PasswordEncoder passwordEncoder) {
    return User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .nickname(nickname)
            .authority(Authority.ROLE_USER)
            .build();
  }

  public UsernamePasswordAuthenticationToken toAuthentication() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }
}

