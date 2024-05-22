package site.soconsocon.notification.fcm.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import site.soconsocon.notification.fcm.domain.entity.DeviceToken;
import site.soconsocon.notification.fcm.domain.entity.DeviceType;
import site.soconsocon.notification.fcm.domain.entity.TokenStatus;

@Data
@AllArgsConstructor
public class SaveTokenRequest {
    private Integer memberId;
    private String token;
    private DeviceType deviceType;

    public DeviceToken toEntity(){
        return DeviceToken.builder()
                .memberId(memberId)
                .deviceToken(token)
                .deviceType(deviceType)
                .status(TokenStatus.ACTIVE)
                .build();
    }
}
