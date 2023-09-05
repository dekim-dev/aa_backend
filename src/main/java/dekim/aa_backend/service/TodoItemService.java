package dekim.aa_backend.service;

import dekim.aa_backend.constant.TodoItemStatus;
import dekim.aa_backend.dto.TodoItemDTO;
import dekim.aa_backend.entity.TodoItem;
import dekim.aa_backend.entity.TodoList;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.TodoItemRepository;
import dekim.aa_backend.persistence.TodoListRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoItemService {

  @Autowired
  private TodoListRepository todoListRepository;

  @Autowired
  private TodoItemRepository todoItemRepository;

  @Autowired
  private UserRepository userRepository;

  @Transactional
  public TodoItem createTodoItem(TodoItemDTO todoItemDTO, Long userId) {

    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found");
    }

    User user = userOptional.get();

    LocalDate createdAt = todoItemDTO.getCreatedAt();
    String listName = user.getId() + createdAt.format(DateTimeFormatter.ofPattern("yyMMdd"));

    // TodoItemDTO를 TodoItem 엔티티로 변환하면서 빌더 활용
    TodoItem todoItem = TodoItem.builder()
            .itemName(todoItemDTO.getItemName())
            .todoItemStatus(todoItemDTO.getTodoItemStatus())
            .timeOfDay(todoItemDTO.getTimeOfDay())
            .priority(todoItemDTO.getPriority())
            .createdAt(todoItemDTO.getCreatedAt())
            .user(user)
            .build();

    // TodoListRepository를 사용하여 리스트 조회 또는 생성
    TodoList existingList = todoListRepository.findByListName(listName);

    if (existingList == null) {
      // 새로운 리스트 생성
      TodoList newList = new TodoList();
      newList.setListName(listName);
      newList.setCreatedAt(createdAt);
      newList.setUser(user);

      TodoList savedList = todoListRepository.save(newList);
      todoItem.setTodoList(savedList); // 새로운 리스트 할당
    } else {
      todoItem.setTodoList(existingList); // 이미 있는 리스트 할당
    }
    todoItemRepository.save(todoItem);
    return todoItem;
  }


  public List<TodoItemDTO> getTodoItemsByDate(LocalDate date, Long userId) {
    // 사용자의 정보 확인
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found");
    }
    User user = userOptional.get();

    List<TodoItem> todoItems = todoItemRepository.findByUserAndCreatedAt(user, date);
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


  public void deleteTodoItemById(Long itemId, Long userId) {
    Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(itemId);

    if (optionalTodoItem.isPresent()) {
      TodoItem todoItem = optionalTodoItem.get();

      if (todoItem.getUser().getId().equals(userId)) {
        todoItemRepository.deleteById(itemId);
      }
    }
  }


  @Transactional
  public void updateTodoItemStatusToDone(Long itemId, Long userId) {
    try {
      TodoItem todoItem = todoItemRepository.findById(itemId)
                      .orElseThrow(() -> new EntityNotFoundException("Todo item not found"));

      TodoItemStatus newStatus = todoItem.getTodoItemStatus() == TodoItemStatus.DONE
              ? TodoItemStatus.NOT_STARTED
              : TodoItemStatus.DONE;

        if (todoItem.getUser().getId().equals(userId)) {
          todoItem.setTodoItemStatus(newStatus);
          todoItemRepository.save(todoItem);        }

    } catch (NoSuchElementException e) {
      throw new RuntimeException("Todo item not found", e);
    }
  }

}


