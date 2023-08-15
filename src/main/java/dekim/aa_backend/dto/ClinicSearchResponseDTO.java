package dekim.aa_backend.dto;

import dekim.aa_backend.entity.Clinic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClinicSearchResponseDTO {
  private List<Clinic> clinics;
  private long totalResults;
}
