package dekim.aa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {
    private Long id;
    private Long userId;
    private String userNickname;
    private String userEmail;
    private Long reportedUserId;
    private String reportedUserNickname;
    private String content;
    private LocalDateTime reportDate;
    private boolean isManaged;
}
