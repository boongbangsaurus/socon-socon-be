package site.soconsocon.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soconsocon.payment.domain.dto.request.PaymentCallbackRequestDto;
import site.soconsocon.payment.service.PaymentService;
import site.soconsocon.utils.MessageUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;


    /**
     * imp_uid(결제 고유 ID), order_uid(주문 고유 ID) 값을 받아 결제 상세 내역을 조회
     *
     * @param paymentCallbackRequestDto
     * @return
     */
    @PostMapping("/validate")
    public ResponseEntity validationPayment(@RequestBody PaymentCallbackRequestDto paymentCallbackRequestDto) {
        return ResponseEntity.ok().body(MessageUtils.success(paymentService.verifyPayment(paymentCallbackRequestDto)));
    }

}
