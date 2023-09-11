package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Table(name = "LIKES_TB")
@Builder
@NoArgsConstructor
public class Likes {
  @Id
  @Column(name = "likesNo")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "postNo")
  @JsonIgnore
  private Post post;

  public Likes(Long id, User user, Post post) {
    this.id = id;
    this.user = user;
    this.post = post;
  }
}

// 한 사용자가 여러개의 좋아요를 할 수 있고
// 한 게시물에 여러개의 좋아요가 있을 수 있음
