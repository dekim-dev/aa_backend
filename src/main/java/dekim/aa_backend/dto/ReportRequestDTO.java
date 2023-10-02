package dekim.aa_backend.dto;

import dekim.aa_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDTO {
    private Long reportedUserId;
    private String content;
    private LocalDateTime reportDate;
}
