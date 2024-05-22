package site.soconsocon.socon.sogon.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.soconsocon.socon.sogon.domain.dto.feign.FcmMessage;

@FeignClient(name = "notification", url = "${feign.urls.notification}")
public interface NotificationFeignClient{
    @PostMapping("/notification/fcm/user")
    public ResponseEntity sendMessageMember(@RequestBody FcmMessage fcmMessage);
}
