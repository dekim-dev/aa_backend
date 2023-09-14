package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "COMMENT_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "commentNo")
  private Long id;

  @Column(nullable = false, length = 1000)
  private String content;

  @CreatedDate
  @Column
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_no")
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_no")
  @JsonIgnore
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinicNo")
  private Clinic clinic;

}