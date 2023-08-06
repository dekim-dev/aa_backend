package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
  private String detailedAddr;

  @Column
  private String tel;

  @Column
  private String intro;

  @Column
  private List<String> schedule;

  @Column
  private double longitude;

  @Column
  private double latitude;

  @Column
  private int viewCount;

  @Column
  private int likes;

  @Column
  private int treatADHD;

  @OneToMany(mappedBy = "clinic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Comment> comments;

}