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
  @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(concat('%', :keyword, '%'))) AND p.boardCategory = :boardCategory")
  Page<Post> searchByTitleOrContentAndBoard(@Param("keyword") String keyword, @Param("boardCategory") String boardCategory, Pageable pageable);

  Page<Post> findByLikesCountGreaterThanEqual(int likesCount, Pageable pageable);

}
