package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "POST_TB")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "postNo")
  private Long id;

  @Column
  private String boardCategory;

  @Column(nullable = false, length = 30)
  private String title;

  @Column(nullable = false, length = 4000)
  private String content;

  @Column(length = 1000)
  private String imgUrl;

  @Column
  private int viewCount;

  @Column
  private int likes;

  @CreationTimestamp
  @Column
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo") // userNo 컬럼을 사용하여 연관 관계 설정
  private User user;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> comments;

}