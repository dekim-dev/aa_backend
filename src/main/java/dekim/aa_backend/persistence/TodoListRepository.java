package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {

  TodoList findByListName(String listName);

}
