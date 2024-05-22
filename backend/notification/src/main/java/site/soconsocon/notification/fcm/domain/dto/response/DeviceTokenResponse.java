package site.soconsocon.notification.fcm.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.soconsocon.notification.fcm.domain.entity.DeviceType;
import site.soconsocon.notification.fcm.domain.entity.TokenStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTokenResponse {
    private Integer memberId;
    private String token;
    private DeviceType deviceType;
    private TokenStatus status;
}
