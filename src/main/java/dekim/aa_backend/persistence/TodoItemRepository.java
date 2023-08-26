package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

  List<TodoItem> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
  Optional<TodoItem> findById(Long id);

}
