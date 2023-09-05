package dekim.aa_backend.dto;

import dekim.aa_backend.constant.TimeOfDay;
import dekim.aa_backend.constant.TodoItemStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TodoItemDTO {
  private Long id;

  private String itemName;

  @Builder.Default // 기본값 설정을 위한 애노테이션 추가
  private TodoItemStatus todoItemStatus = TodoItemStatus.NOT_STARTED; // 기본값 설정

  private TimeOfDay timeOfDay;

  private LocalDate createdAt;

  private int priority;
}
