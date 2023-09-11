package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

  Optional<Likes> findByUserIdAndPostId(Long userId, Long postId);
}
