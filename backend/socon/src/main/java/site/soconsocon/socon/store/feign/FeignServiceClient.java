package site.soconsocon.socon.store.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.soconsocon.socon.store.domain.dto.request.ChargeRequest;
import site.soconsocon.socon.store.domain.dto.request.RoleRequest;
import site.soconsocon.socon.store.domain.entity.feign.Member;

@FeignClient(name = "user", url = "${feign.urls.user}")
public interface FeignServiceClient {

    // 멤버 정보 조회
    @GetMapping("/members/{memberId}")
    Member getMemberInfo(@PathVariable("memberId") int memberId);

    // 출금 요청
    @PutMapping("/accounts/deposit")
    void deposit(@RequestBody ChargeRequest request);

    // 사장님 인증 role 변경
    @PutMapping("/members/role")
    void changeRole(@RequestBody RoleRequest request);



}
