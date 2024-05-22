package site.soconsocon.socon.store.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity(name="BUSINESS_REGISTRATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BusinessRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_number_id", nullable = false)
    private Integer id;

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    @Column(name = "registration_address", nullable = false)
    private String registrationAddress;

    @Column(name = "member_id", nullable = false)
    private Integer memberId; // 멤버 id

}
