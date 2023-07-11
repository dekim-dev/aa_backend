package dekim.aa_backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLINIC_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clinic {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "clinicNo")
  private Long id;

  @Column
  private String name;

  @Column
  private String address;

  @Column
  private String specialist;

}