package site.soconsocon.notification.fcm.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import site.soconsocon.notification.fcm.domain.dto.response.DeviceTokenResponse;
import site.soconsocon.notification.global.domain.dto.request.Member;
import site.soconsocon.notification.global.feign.MemberFeignClient;
import site.soconsocon.notification.global.feign.dto.response.MemberFeignResponse;

/**
 * 사용자에게 Firebase Cloud Message 알람을 송신하기 위한 엔티티
 * id(int)- 12
 */
@Entity
@Getter @Setter
@Table(name="device_token", indexes = @Index(columnList="member_id"))
@ToString(of = {})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id", nullable = false)
    private Integer Id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Transient
    private Member member;

    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;


    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @Column(name = "device_token",nullable = true, length = 256, unique = true)
    private String deviceToken;

    public void memberOf(Member member){
        this.member=member;
    }
    public void memberOf(MemberFeignResponse memberFeignResponse){
        this.member=Member.builder()
                .memberId(memberFeignResponse.getMemberId())
                .email(memberFeignResponse.getEmail())
                .nickname(memberFeignResponse.getNickname())
                .soconMoney(memberFeignResponse.getSoconMoney())
                .soconPassword(memberFeignResponse.getSoconPassword())
                .build();
    }

    public DeviceTokenResponse toDto(){
        return DeviceTokenResponse.builder()
                .memberId(memberId)
                .token(deviceToken)
                .deviceType(deviceType)
                .status(status)
                .build();
    }

}
