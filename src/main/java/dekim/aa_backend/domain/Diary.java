package dekim.aa_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "DIARY_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {
  @Id
  @Column(name = "diaryNo")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 20)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String conclusion;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  private User user;

}