package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.User;
import dekim.aa_backend.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    Optional<UserReport> findByReporterAndReportedUser(User reporter, User reportedUser);

}
