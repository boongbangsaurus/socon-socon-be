package site.soconsocon.payment.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import site.soconsocon.payment.service.feign.response.MemberFeignResponse;

@FeignClient(name = "memberClient", url = "${feign.urls.member}")
public interface MemberFeignClient {

    @GetMapping("/{memberId}")
    MemberFeignResponse findMemberIdByMemberId(@PathVariable int memberId);

}
