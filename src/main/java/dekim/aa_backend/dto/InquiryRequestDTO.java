package dekim.aa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequestDTO {
    private Long id;
    private String title;
    private String content;
    private boolean isAnswered;
    private Long userId;
    private String userNickname;
}
