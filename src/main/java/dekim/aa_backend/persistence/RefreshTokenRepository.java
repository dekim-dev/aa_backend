package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByUserEmail(String userEmail);
  Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
