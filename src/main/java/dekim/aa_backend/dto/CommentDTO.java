package dekim.aa_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
  private String nickname;
  private Long id;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long userId;
  private String pfImg;
  private Long postId;
  private Long clinicId;

  public CommentDTO(String content) { // 댓글 수정용
    this.content = content;
  } // 댓글 수정용
}