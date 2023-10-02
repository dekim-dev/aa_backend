package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.User;
import dekim.aa_backend.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Optional<UserBlock> findByBlockerAndBlockedUser(User blocker, User blockedUser);

}
