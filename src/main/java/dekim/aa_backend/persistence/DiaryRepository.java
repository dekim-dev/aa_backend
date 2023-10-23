package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<Diary> findTop3ByUserOrderByCreatedAtDesc(User user);
}
