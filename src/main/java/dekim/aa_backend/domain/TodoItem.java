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
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "todoItemNo")
  private Long id;

  @Column(nullable = false)
  private String item;

  @Column
  private int priority;

  @Enumerated(EnumType.STRING)
  private TodoItemStatus todoItemStatus;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;


}
