package dekim.aa_backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClinicDTO {
  private Long id;
  private String hpid;
  private String name;
  private String address;
  private String detailedAddr;
  private String tel;
  private String info;
  private double latitude;
  private double longitude;
  private int viewCount;
  private String scheduleJson;
  private int recommendCount;
  private List<CommentDTO> commentDTOs;
}

