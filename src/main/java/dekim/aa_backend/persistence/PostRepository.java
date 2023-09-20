package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByBoardCategory(String boardCategory, Pageable pageable);
  Page<Post> findByUserId(Long userId, Pageable pageable);
  @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
  Page<Post> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);
}
