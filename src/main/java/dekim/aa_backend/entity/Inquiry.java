package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "INQUIRY_TB")
@Getter
@Setter
@ToString
public class Inquiry {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column
    private LocalDateTime inquiryDate;

    @Column
    private boolean isAnswered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;
}
