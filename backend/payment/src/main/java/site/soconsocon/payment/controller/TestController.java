package site.soconsocon.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soconsocon.payment.service.feign.MemberFeignClient;
import site.soconsocon.payment.service.feign.SoconFeignClient;
import site.soconsocon.payment.service.feign.request.AddMySoconRequest;
import site.soconsocon.payment.service.feign.response.MemberFeignResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/test")
@Log4j2
public class TestController {

    private final MemberFeignClient memberFeignClient;

    private final SoconFeignClient soconFeignClient;

    @GetMapping("")
    public void testFeign(@RequestHeader("X-Authorization-Id") int memberId) {
        MemberFeignResponse memberIdByMemberId = memberFeignClient.findMemberIdByMemberId(memberId);
        System.out.println(memberIdByMemberId);
    }

    @GetMapping("/save")
    public String testSave(@RequestHeader("X-Authorization-Id") int memberId) {
//        AddMySoconRequest addMySoconRequest = AddMySoconRequest.builder()
//                .purchaseAt(LocalDateTime.now())
//                .expiredAt(LocalDateTime.now().plusDays(30))
//                .issueId(1)
//                .status("unused")
//                .memberId(memberId)
//                .purchasedQuantity(1)
//                .build();
        AddMySoconRequest addMySoconRequest1 = new AddMySoconRequest(LocalDateTime.now(), LocalDateTime.now().plusDays(30), null, "unused", memberId, 1, 1);
        log.info("addMySoconRequest: {}", addMySoconRequest1);
        soconFeignClient.saveMySocon(addMySoconRequest1);

        return "saveMethod";
    }


}
