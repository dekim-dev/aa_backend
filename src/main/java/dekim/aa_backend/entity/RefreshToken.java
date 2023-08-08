package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "REFRESH_TOKEN_TB")
@AllArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "userEmail", nullable = false, unique = true)
  private String userEmail;

  @Column(name = "refreshToken", nullable = false)
  private String refreshToken;

  public RefreshToken(String refreshToken, String userEmail) {
    this.refreshToken = refreshToken;
    this.userEmail = userEmail;
  }

  public RefreshToken updateToken(String newRefreshToken) {
    this.refreshToken = newRefreshToken;
    return this;
  }
}
