package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "REFRESH_TOKEN_TB")
public class RefreshToken {

  @Id
  @Column(name = "`key`")
  private String key;

  @Column(name = "`value`")
  private String value;

  private Long expiresIn;


  @Builder
  public RefreshToken(String key, String value, Long expiresIn) {
    this.key = key;
    this.value = value;
    this.expiresIn = expiresIn;
  }

  public RefreshToken updateValue(String token) {
    this.value = token;
    return this;
  }

}
