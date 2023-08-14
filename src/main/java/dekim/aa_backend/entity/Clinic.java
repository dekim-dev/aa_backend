package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "CLINIC_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Clinic {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "clinicNo")
  private Long id;

  @Column
  private String hpid;

  @Column
  private String name;

  @Column
  private String address;

  @Column
  private String detailedAddr;

  @Column
  private String tel;

  @Column
  private String info;

  @Column(columnDefinition = "TEXT")
  private String scheduleJson;

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

  @Builder
  public Clinic(String hpid, String name, String address, String detailedAddr, String tel, String info,
                double latitude, double longitude) {
    this.hpid = hpid;
    this.name = name;
    this.address = address;
    this.detailedAddr = detailedAddr;
    this.tel = tel;
    this.info = info;
    this.latitude = latitude;
    this.longitude = longitude;
  }
}