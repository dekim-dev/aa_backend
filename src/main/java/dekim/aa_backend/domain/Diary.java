package dekim.aa_backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "memberNO")
  private Member member;

}