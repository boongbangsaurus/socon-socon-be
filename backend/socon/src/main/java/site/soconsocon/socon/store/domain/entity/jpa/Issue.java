package site.soconsocon.socon.store.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name="ISSUE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name; // 상품명

    @Column(name = "store_name", nullable = false)
    private String storeName; // 가게 이름

    @Column(name = "image")
    private String image;

    @Column(name = "is_main", nullable = false, columnDefinition = "boolean default false")
    private Boolean isMain; // 대표상품여부

    @Column(name = "price", nullable = false)
    private Integer price; // 상품 가격(정가)

    @Column(name = "is_discounted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDiscounted; // 할인 적용 여부

    @Column(name = "discounted_price")
    private Integer discountedPrice; // 할인된 가격

    @Column(name = "max_quantity", nullable = false)
    private Integer maxQuantity; // 설정된 최대발행량

    @Column(name = "issued_quantity", nullable = false, columnDefinition = "integer default 0")
    private Integer issuedQuantity; // 현재 발행량

    @Column(name = "used", nullable = false, columnDefinition = "integer default 0")
    private Integer used; // 사용된 개수

    @Column(name = "period", nullable = false)
    private Integer period; // 사용 기간

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt; // 등록 일자

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "char default 'A'")
    private IssueStatus status; // 발행 상태. a:active i:inactive

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "issue")
    @Builder.Default
    private List<Socon> socons = new ArrayList<>();

}