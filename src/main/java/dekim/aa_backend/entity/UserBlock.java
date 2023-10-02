package dekim.aa_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "BLOCK_TB")
@Getter
@Setter
@ToString
public class UserBlock {
    @Id
    @GeneratedValue
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User blockedUser;
}
