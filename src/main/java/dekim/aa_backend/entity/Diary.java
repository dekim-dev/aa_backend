package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "DIARY_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @ElementCollection
  private List<String> med;

  @ElementCollection
  private List<LocalTime> takenAt;

  @ElementCollection
  @CollectionTable(name = "MEDICATIONS", joinColumns = @JoinColumn(name = "diary_id"))
  @Column(name = "medication")
  private List<String> medications;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @JsonIgnore
  private User user;

}