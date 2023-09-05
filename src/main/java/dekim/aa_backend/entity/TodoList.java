package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "TODO_LIST_TB")
@RequiredArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoList {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "listNo")
  private Long id;

  @Column(nullable = false)
  private String listName;

  @CreatedDate
  @Column
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  private User user;

  @OneToMany(mappedBy = "todoList")
  @JsonIgnore
  private List<TodoItem> todoItems = new ArrayList<>();

  @Builder
  public TodoList(Long id, String listName, LocalDate createdAt, User user, List<TodoItem> todoItems) {
    this.id = id;
    this.listName = listName;
    this.createdAt = createdAt;
    this.user = user;
    this.todoItems = todoItems;
  }
}
