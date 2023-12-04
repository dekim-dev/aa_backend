package dekim.aa_backend.dto;

import dekim.aa_backend.entity.Likes;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
  private int likesCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String nickname;
  private Long userId;
  private String pfImg;
  private List<CommentDTO> commentsDTO; // 회원 정보를 받아오기 위해 DTO 로 받기
  private Set<Likes> likes;
  private int reportCount;
  private int commentsCount;
}
