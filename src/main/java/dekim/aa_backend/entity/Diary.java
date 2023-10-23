package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

  @Column
  private LocalDateTime createdAt;

  @Column
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "diary", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<MedicationList> medicationLists = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @JsonIgnore
  private User user;

}