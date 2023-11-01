package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT_TB")
@Getter
@Setter
@ToString
public class UserReport {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column
    private LocalDateTime reportDate;

    @Column
    private boolean isManaged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User reportedUser;
}
