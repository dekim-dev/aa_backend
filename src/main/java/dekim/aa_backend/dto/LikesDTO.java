package dekim.aa_backend.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class LikesDTO {
  private Long userId;
  private Long postId;
  private boolean isAdded;

  @Builder
  public LikesDTO(Long userId, Long postId, boolean isAdded) {
    this.userId = userId;
    this.postId = postId;
    this.isAdded = isAdded;
  }
}
