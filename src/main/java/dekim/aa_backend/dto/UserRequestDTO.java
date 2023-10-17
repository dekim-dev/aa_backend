package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Authority;
import dekim.aa_backend.entity.User;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

  private String email;
  private String password;
  private String nickname;
  private String authKey;
  private String pfImg;

  public User toUser(PasswordEncoder passwordEncoder) {
    return User.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .nickname(nickname)
            .pfImg("https://firebasestorage.googleapis.com/v0/b/appropriate-attention.appspot.com/o/default_images%2Fdefault_pfImg.svg?alt=media")
            .authKey(authKey)
            .authority(Authority.ROLE_USER)
            .build();
  }

  public UsernamePasswordAuthenticationToken toAuthentication() {
    return new UsernamePasswordAuthenticationToken(email, password);
  }
}

