package site.soconsocon.socon.store.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "SOCON")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Socon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "socon_id", updatable = false, nullable = false)
    private Integer id;

    @Column (name="purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    @Column (name="expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column (name="used_at")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column (name="status", nullable = false)
    private SoconStatus status;

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 멤버 id

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

}