package dekim.aa_backend.controller;
import dekim.aa_backend.dto.TodoItemDTO;
import dekim.aa_backend.entity.TodoItem;
import dekim.aa_backend.service.TodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/todo-item")
public class TodoItemController {

  @Autowired
  private TodoItemService todoItemService;

  @PostMapping
  public ResponseEntity<?> createTodoItem(@RequestBody TodoItemDTO todoItemDTO) {
    try {
      TodoItem item = todoItemService.createTodoItem(todoItemDTO);
      return new ResponseEntity<>(item, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("투두아이템 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/items")
  public ResponseEntity<List<TodoItemDTO>> getTodoItemsByDateRange(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<TodoItemDTO> todoItems = todoItemService.getTodoItemsByDateRange(startDate, endDate);
    return ResponseEntity.ok(todoItems);
  }

  @DeleteMapping("/{itemId}")
  public ResponseEntity<String> deleteTodoItem(@PathVariable Long itemId) {
    try {
      todoItemService.deleteTodoItemById(itemId);
      return ResponseEntity.ok("투두 아이템 삭제 성공");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("투두 아이템 삭제 실패..");
    }
  }

  @PutMapping("/{itemId}")
  public ResponseEntity<String> markItemAsDone(@PathVariable Long itemId) {
    try {
      todoItemService.updateTodoItemStatusToDone(itemId);
      return ResponseEntity.ok("투두 아이템 상태 변경 완료");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("존재하지 않는 투두아이템 ID");
    }
  }

}