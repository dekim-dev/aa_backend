package dekim.aa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
  private String grantType;
  private String accessToken;
  private Long accessTokenExpiresIn;
  private String refreshToken;
  private Long refreshTokenExpiresIn;
  private String authority;
}
