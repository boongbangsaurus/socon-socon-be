package site.soconsocon.socon.sogon.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import site.soconsocon.socon.store.domain.entity.jpa.Socon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name="SOGON")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Sogon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sogon_id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "is_picked", nullable = false, columnDefinition = "boolean default false")
    private Boolean isPicked; // 채택 소곤 여부

    @Column(name = "image1")
    private String image1; // 이미지 1

    @Column(name = "image2")
    private String image2; // 이미지 2

    @Column(name = "lat", nullable = false)
    private Double lat; // 위도

    @Column(name = "lng", nullable = false)
    private Double lng; // 경도

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 멤버 id

    @OneToOne
    @JoinColumn(name = "socon_id")
    private Socon socon;

    @OneToMany(mappedBy = "sogon")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

}