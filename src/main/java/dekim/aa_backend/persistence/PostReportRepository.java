package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.PostReport;
import dekim.aa_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    Optional<PostReport> findByUserAndPost(User user, Post post);
}
