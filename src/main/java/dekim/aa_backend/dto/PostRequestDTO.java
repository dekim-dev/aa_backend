package dekim.aa_backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {
  private Long id;
  private String boardCategory;
  private String topic;
  private String title;
  private String content;
  private String imgUrl;
  private String nickname;
}
