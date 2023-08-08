package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "userEmail", nullable = false, unique = true)
  private String userEmail;

  @Column(name = "refreshToken", nullable = false)
  private String refreshToken;

  public RefreshToken(String userEmail, String refreshToken) {
    this.userEmail = userEmail;
    this.refreshToken = refreshToken;
  }

  public RefreshToken updateToken(String newRefreshToken) {
    this.refreshToken = newRefreshToken;
    return this;
  }
}
