package dekim.aa_backend.domain;

import dekim.aa_backend.constant.TodoItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoList {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "todoListNo")
  private Long id;

  @Column(nullable = false)
  private String todoListName;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;


}
