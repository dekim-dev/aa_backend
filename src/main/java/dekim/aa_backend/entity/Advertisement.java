package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "AD_TB")
@Getter
@Setter
@ToString
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String advertiser;

    @Column
    private String imgUrl;

    @Column
    private LocalDate expiresOn;
}
