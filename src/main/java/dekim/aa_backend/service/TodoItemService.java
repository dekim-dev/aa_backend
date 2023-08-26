package dekim.aa_backend.service;

import dekim.aa_backend.constant.TodoItemStatus;
import dekim.aa_backend.dto.TodoItemDTO;
import dekim.aa_backend.entity.TodoItem;
import dekim.aa_backend.entity.TodoList;
import dekim.aa_backend.persistence.TodoItemRepository;
import dekim.aa_backend.persistence.TodoListRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TodoItemService {

  @Autowired
  private TodoListRepository todoListRepository;

  @Autowired
  private TodoItemRepository todoItemRepository;

  @Transactional
  public TodoItem createTodoItem(TodoItemDTO todoItemDTO) {
    LocalDateTime createdAt = todoItemDTO.getCreatedAt();
    String listName = createdAt.format(DateTimeFormatter.ofPattern("yyMMdd"));

    // TodoItemDTO를 TodoItem 엔티티로 변환하면서 빌더 활용
    TodoItem todoItem = TodoItem.builder()
            .itemName(todoItemDTO.getItemName())
            .todoItemStatus(todoItemDTO.getTodoItemStatus())
            .timeOfDay(todoItemDTO.getTimeOfDay())
            .priority(todoItemDTO.getPriority())
            .createdAt(todoItemDTO.getCreatedAt())
            .build();

    // TodoListRepository를 사용하여 리스트 조회 또는 생성
    TodoList existingList = todoListRepository.findByListName(listName);

    if (existingList == null) {
      // 새로운 리스트 생성
      TodoList newList = new TodoList();
      newList.setListName(listName);
      newList.setCreatedAt(createdAt);

      TodoList savedList = todoListRepository.save(newList);
      todoItem.setTodoList(savedList); // 새로운 리스트 할당
    } else {
      todoItem.setTodoList(existingList); // 이미 있는 리스트 할당
    }
    todoItemRepository.save(todoItem);
    return todoItem;
  }

  public List<TodoItemDTO> getTodoItemsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    List<TodoItem> todoItems = todoItemRepository.findByCreatedAtBetween(startDate, endDate);
    return todoItems.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
  }

  private TodoItemDTO convertToDto(TodoItem todoItem) {
    TodoItemDTO dto = new TodoItemDTO();
    dto.setId(todoItem.getId());
    dto.setItemName(todoItem.getItemName());
    dto.setTodoItemStatus(todoItem.getTodoItemStatus());
    dto.setTimeOfDay(todoItem.getTimeOfDay());
    dto.setPriority(todoItem.getPriority());
    dto.setCreatedAt(todoItem.getCreatedAt());
    return dto;
  }

  public void deleteTodoItemById(Long itemId) {
    todoItemRepository.deleteById(itemId);
  }

  @Transactional
  public void updateTodoItemStatusToDone(Long itemId) {
    try {
      TodoItem todoItem = todoItemRepository.findById(itemId)
                      .orElseThrow(() -> new EntityNotFoundException("Todo item not found"));

      TodoItemStatus newStatus = todoItem.getTodoItemStatus() == TodoItemStatus.DONE
              ? TodoItemStatus.NOT_STARTED
              : TodoItemStatus.DONE;

      todoItem.setTodoItemStatus(newStatus);
      todoItemRepository.save(todoItem);

    } catch (NoSuchElementException e) {
      throw new RuntimeException("Todo item not found", e);
    }
  }

}


