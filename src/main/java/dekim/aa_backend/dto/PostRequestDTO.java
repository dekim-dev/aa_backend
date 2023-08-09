package dekim.aa_backend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {
  private String boardCategory;
  private String title;
  private String content;
  private String imgUrl;
  private String nickname;
}
