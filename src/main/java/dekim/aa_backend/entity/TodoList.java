package dekim.aa_backend.entity;

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
@Table(name = "TODO_LIST_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoList {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "listNo")
  private Long id;

  @Column(nullable = false)
  private String listName;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  private User user;

//  @OneToMany(mappedBy = "todoItem")
//  private List<TodoList> todoLists = new ArrayList<>();


}
