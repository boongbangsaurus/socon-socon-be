package site.soconsocon.notification.fcm.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import site.soconsocon.notification.fcm.domain.entity.DeviceToken;
import site.soconsocon.notification.fcm.domain.entity.TokenStatus;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<DeviceToken,Integer> {
    Optional<List<DeviceToken>> findDeviceTokensByMemberId(Integer memberId);
    Optional<List<DeviceToken>> findAllByStatus(TokenStatus status);
}
