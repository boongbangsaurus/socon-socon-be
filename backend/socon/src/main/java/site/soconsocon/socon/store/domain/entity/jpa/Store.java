package site.soconsocon.socon.store.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name="STORE")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "businessHours")
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category; // 가게 분류

    @Column(name = "image")
    private String image; // 가게 대표 이미지

    @Column(name = "phone_number")
    private String phoneNumber; // 가게 전화번호

    @Column(name = "address", nullable = false)
    private String address; // 가게 주소

    @Column(name = "lat", nullable = false)
    private Double lat; // 위도

    @Column(name = "lng", nullable = false)
    private Double lng; // 경도

    @Column(name = "introduction")
    private String introduction; // 가게 설명

    @Column(name = "closing_planned")
    private LocalDate closingPlanned; // 폐업 예정 일자

    @Column(name = "is_closed", nullable = false, columnDefinition = "boolean default false")
    private Boolean isClosed; // 폐업 여부

    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt; // 등록일

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 멤버 id

    @ManyToOne
    @JoinColumn(name = "registration_number_id")
    private BusinessRegistration businessRegistration;

    @OneToMany(mappedBy = "store")
    @Builder.Default
    private List<BusinessHour> businessHours = new ArrayList<>();

}
