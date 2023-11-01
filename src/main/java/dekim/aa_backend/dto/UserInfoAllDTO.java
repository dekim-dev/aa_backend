package dekim.aa_backend.dto;

import dekim.aa_backend.constant.IsPaidMember;
import dekim.aa_backend.entity.Comment;
import dekim.aa_backend.entity.Likes;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.UserBlock;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoAllDTO {
  private Long id;
  private String pfImg;
  private String nickname;
  private String email;
  private LocalDateTime regDate;
  private IsPaidMember isPaidMember;
  private int postCount;
  private int commentCount;
  private List<Post> posts;
  private List<Comment> comments;
  private Set<Likes> likes;
  private List<UserBlock> blockedUsers;
  private List<String> blockedUserNicknames;
}
