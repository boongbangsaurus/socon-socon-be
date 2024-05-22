package site.soconsocon.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.auth.feign.domain.dto.feign.SaveTokenRequest;

@FeignClient(name = "notification", url = "${feign.urls.notification}")
public interface NotificationFeignClient {

    @PostMapping("/notification/fcm/save")
    ResponseEntity saveDeviceToken(@RequestBody SaveTokenRequest saveTokenRequest);

}
