package dekim.aa_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClinicRequestDTO {
  private String hpid;
  private String name;
  private String address;
  private String detailedAddr;
  private String tel;
  private String info;
  private String scheduleJson;
  private double longitude;
  private double latitude;
}

