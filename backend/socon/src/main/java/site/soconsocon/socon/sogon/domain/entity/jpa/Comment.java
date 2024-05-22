package site.soconsocon.socon.sogon.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name="COMMENT")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Integer id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_picked")
    private Boolean isPicked;

    @ManyToOne
    @JoinColumn(name = "sogon_id")
    private Sogon sogon;

    @Column(name = "member_id")
    private Integer memberId;
}
