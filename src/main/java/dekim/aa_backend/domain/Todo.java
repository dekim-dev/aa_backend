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
public class Todo {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "todoNo")
        private Long id;

}
