package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "POST_TB")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "postNo")
  private Long id;

  @Column
  private String boardCategory;

  @Column
  private String topic;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, length = 4000)
  private String content;

  @Column(length = 1000)
  private String imgUrl;

  @Column
  private int viewCount;

  @Column(nullable = true)
  private int likesCount;

  @CreationTimestamp
  @Column
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;

  @Column
  private int reportCount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo") // userNo 컬럼을 사용하여 연관 관계 설정
  @JsonIgnore
  private User user;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Fetch(FetchMode.SUBSELECT)
  private List<Comment> comments;

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Likes> likes = new HashSet<>();

  @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<PostReport> reports = new HashSet<>();
}