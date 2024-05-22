package site.soconsocon.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import site.soconsocon.auth.feign.domain.dto.feign.MyPageSoconResponse;

@FeignClient(name = "socon", url = "${feign.urls.socon}")
public interface SoconFeignClient {

    @GetMapping("/socons/mypage/{member_id}")
    ResponseEntity<MyPageSoconResponse> myPage(@PathVariable("member_id") Integer memberId);

}
