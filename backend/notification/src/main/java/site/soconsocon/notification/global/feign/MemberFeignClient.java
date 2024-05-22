package site.soconsocon.notification.global.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.soconsocon.notification.global.feign.dto.response.MemberFeignResponse;

/**
 * user service 연결 통해 사용자 정보 받아오는 API
 */
@FeignClient(name = "user-service", path = "/api/v1/members")
public interface MemberFeignClient {

    @GetMapping
    MemberFeignResponse getMemberByMemberEmail(@RequestParam("email") String memberEmail);

}
