package dekim.aa_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "MEMBER_DIARY_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDiary {
  @Id
  @Column(name = "diaryItemNo")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 20)
  private String title;

  @Column(nullable = false, length = 4000)
  private String content;

  @Column(nullable = false)
  private String emotion;

  @Column(nullable = false, length = 30)
  private String comment;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;
}