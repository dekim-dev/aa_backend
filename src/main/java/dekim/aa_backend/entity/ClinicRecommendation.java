package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Table(name = "RECOMMENDATION_TB")
@Builder
@NoArgsConstructor
public class ClinicRecommendation {
  @Id
  @Column(name = "recommendationNo")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userNo")
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "clinicNo")
  @JsonIgnore
  private Clinic clinic;

  public ClinicRecommendation(Long id, User user, Clinic clinic) {
    this.id = id;
    this.user = user;
    this.clinic = clinic;
  }
}