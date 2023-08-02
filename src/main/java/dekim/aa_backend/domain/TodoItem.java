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
@Table(name = "TODO_ITEM_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ItemNo")
  private Long id;

  @Column(nullable = false)
  private String itemName;

  @Enumerated(EnumType.STRING)
  private TodoItemStatus todoItemStatus;

  @Column
  private int priority;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "listNo")
  private TodoList todoList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberNo")
  private Member member;


}
