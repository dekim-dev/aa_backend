package dekim.aa_backend.controller;
import dekim.aa_backend.dto.TodoItemDTO;
import dekim.aa_backend.entity.TodoItem;
import dekim.aa_backend.service.TodoItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("/todo-item")
public class TodoItemController {

  @Autowired
  private TodoItemService todoItemService;

  @PostMapping
  public ResponseEntity<?> createTodoItem(@AuthenticationPrincipal UserDetails userDetails, @RequestBody TodoItemDTO todoItemDTO) {
    try {
        if (userDetails == null) {
          // 사용자 정보가 없는 경우 처리
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
      TodoItem item = todoItemService.createTodoItem(todoItemDTO, Long.valueOf(userDetails.getUsername()));
      return new ResponseEntity<>(item, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("투두아이템 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/items")
  public ResponseEntity<?> getTodoItemsByDateRange(
          @AuthenticationPrincipal UserDetails userDetails,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
          ) {

    try {
      if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }
      Long userId = Long.valueOf(userDetails.getUsername());

      List<TodoItemDTO> todoItems = todoItemService.getTodoItemsByDate(date, userId);
      return ResponseEntity.ok(todoItems);
    } catch (Exception e) {
      log.warn("Error creating todo item: ", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<String> deleteTodoItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemId) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    Long userId = Long.valueOf(userDetails.getUsername());
    todoItemService.deleteTodoItemById(itemId, userId);

      return ResponseEntity.ok("투두 아이템 삭제 성공");
  }


  @PutMapping("/{itemId}")
  public ResponseEntity<String> markItemAsDone(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemId) {
    try {
      if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }

      Long userId = Long.valueOf(userDetails.getUsername());
      todoItemService.updateTodoItemStatusToDone(itemId, userId);
      return ResponseEntity.ok("투두 아이템 상태 변경 완료");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("존재하지 않는 투두아이템 ID");
    }
  }

}