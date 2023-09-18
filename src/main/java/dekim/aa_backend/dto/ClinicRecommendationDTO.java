package dekim.aa_backend.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class ClinicRecommendationDTO {
  private Long userId;
  private Long clinicId;
  private boolean isRecommended;

  @Builder
  public ClinicRecommendationDTO(Long userId, Long clinicId, boolean isRecommended) {
    this.userId = userId;
    this.clinicId = clinicId;
    this.isRecommended = isRecommended;
  }
}
