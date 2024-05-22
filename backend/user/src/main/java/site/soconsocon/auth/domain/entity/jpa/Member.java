package site.soconsocon.auth.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role; //USER, MANAGER, ADMIN

    @Column(nullable = false)
    private String phoneNumber;

    private String profileUrl; // 프로필 사진

    @Column(columnDefinition = "boolean default false")
    private boolean isAgreed; //약관동의 여부(개인정보 + 서비스)

    private String accountNo; //출금계좌

    private int soconMoney; //소콘 머니

    private String soconPassword; //소콘 비밀번호

    public void updateRole(UserRole role) { //권한 변경
        this.role = role;
    }

}
