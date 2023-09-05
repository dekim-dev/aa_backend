package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dekim.aa_backend.constant.TimeOfDay;
import dekim.aa_backend.constant.TodoItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "TODO_ITEM_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "itemNo")
  private Long id;

  @Column(nullable = false)
  private String itemName;

  @Enumerated(EnumType.STRING)
  private TodoItemStatus todoItemStatus;

  @Enumerated(EnumType.STRING)
  private TimeOfDay timeOfDay;

  @Column
  private int priority;

  @CreatedDate
  @Column
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listNo")
  @JsonIgnore
  private TodoList todoList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @JsonIgnore
  private User user;

  // 빌더 추가
  @Builder
  public TodoItem(String itemName, TodoItemStatus todoItemStatus, TimeOfDay timeOfDay, int priority, LocalDate createdAt, TodoList todoList, User user) {
    this.itemName = itemName;
    this.todoItemStatus = todoItemStatus;
    this.timeOfDay = timeOfDay;
    this.priority = priority;
    this.createdAt = createdAt;
    this.todoList = todoList;
    this.user = user;
  }
}

