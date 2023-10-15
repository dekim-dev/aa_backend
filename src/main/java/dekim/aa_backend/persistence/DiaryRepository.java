package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByUser(User user);
    List<Diary> findTop3ByUserOrderByCreatedAtDesc(User user);

}
