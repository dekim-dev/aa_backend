package dekim.aa_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "POST_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "postNo")
  private Long id;

  @Column(nullable = false)
  private String boardCategory;

  @Column(nullable = false, length = 30)
  private String title;

  @Column(nullable = false, length = 4000)
  private String content;

  @Column(length = 1000)
  private String imgUrl;

  @Column
  private int viewCount;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberNo")
  private Member member;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Reply> replies;

}