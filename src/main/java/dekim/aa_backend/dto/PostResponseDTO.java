package dekim.aa_backend.dto;

import dekim.aa_backend.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
  private Long id;
  private String boardCategory;
  private String topic;
  private String title;
  private String content;
  private String imgUrl;
  private int viewCount;
  private int likes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String nickname;
  private List<Comment> comments;
}
