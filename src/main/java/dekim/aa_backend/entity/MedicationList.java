package dekim.aa_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "MEDICATION_TB")
public class MedicationList {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String med;

    @Column
    private LocalTime takenAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaryNo")
    @JsonIgnore
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    @JsonIgnore
    private User user;
}
