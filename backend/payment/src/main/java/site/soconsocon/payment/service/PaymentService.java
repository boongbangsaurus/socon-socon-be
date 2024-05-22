package site.soconsocon.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.soconsocon.payment.domain.dto.response.PaymentResponseDto;
import site.soconsocon.payment.domain.dto.request.PaymentCallbackRequestDto;
import site.soconsocon.payment.domain.entity.jpa.Orders;
import site.soconsocon.payment.exception.ErrorCode;
import site.soconsocon.payment.exception.PaymentException;
import site.soconsocon.payment.service.feign.SoconFeignClient;
import site.soconsocon.payment.service.feign.request.AddMySoconRequest;
import site.soconsocon.payment.repository.OrderRepository;
import site.soconsocon.payment.repository.PaymentRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final SoconFeignClient soconFeignClient;

    private IamportClient iamportClient;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

//    public PaymentByOrderResponseDto findOrderByImpUid(String impUid) throws PaymentException {
//        //결제 정보 가져오기
//        site.soconsocon.payment.domain.entity.jpa.Payment payment = paymentRepository.findPaymentByImpUid(impUid).orElseThrow(
//                () -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND)
//        );
//        //주문내역 조회
//        Order order = orderRepository.findOrderByImpUid(impUid)
//                .orElseThrow(() -> new PaymentException(ErrorCode.ORDER_NOT_FOUND));
//
//        PaymentByOrderResponseDto paymentByOrderResponseDto = PaymentByOrderResponseDto.builder()
//                .id(payment.getId())
//                .impUid(payment.getImpUid())
//                .amount(payment.getAmount())
//                .orderUid(payment.getOrderUid())
//                .itemName(payment.getItemName())
//                .build();
//
//        return paymentByOrderResponseDto;
//    }

    //    }
    public PaymentResponseDto verifyPayment(PaymentCallbackRequestDto paymentCallbackRequestDto) {
        String impUid = paymentCallbackRequestDto.getImpUid(); //결제 고유번호
        String orderUid = paymentCallbackRequestDto.getOrderUid(); //주문 고유번호

        try {
            // 결제 단건 조회(아임포트)
            // 결제 ImpUid Check
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);

            int amount = iamportResponse.getResponse().getAmount().intValue(); //결제 금액
            String itemName = iamportResponse.getResponse().getName(); //상품명
            String status = iamportResponse.getResponse().getStatus(); //paid이면 1

            //주문내역 조회
            Orders order = orderRepository.findOrderByOrderUid(orderUid)
                    .orElseThrow(() -> new PaymentException(ErrorCode.ORDER_NOT_FOUND));

            PaymentResponseDto paymentDto = PaymentResponseDto.builder()
                    .impUid(impUid)
                    .amount(amount)
                    .status(status)
                    .itemName(itemName)
                    .build();

            if (iamportResponse.getResponse().getStatus().equals("paid")) { //결제가 정상적으로 이루어졌다면

                int price = order.getPrice(); // DB에 저장된 결제 금액
                int iamportPrice = iamportResponse.getResponse().getAmount().intValue(); // 실 결제 금액

                // 결제 금액 검증
                if (iamportPrice != price) {
                    // 주문, 결제 삭제
                    orderRepository.updateOrderStatus(paymentCallbackRequestDto.getOrderUid(), "FAIL"); //Fail로 변경

                    // 결제금액 위변조로 의심되는 결제금액을 취소(아임포트)
                    iamportClient.cancelPaymentByImpUid(new CancelData(iamportResponse.getResponse().getImpUid(), true, new BigDecimal(iamportPrice)));

                    throw new PaymentException(ErrorCode.PAYMENT_AMOUNT_NOT_SAME); //결제 실패
                }
                // DTO -> Entity 변환
                site.soconsocon.payment.domain.entity.jpa.Payment payment = new site.soconsocon.payment.domain.entity.jpa.Payment(paymentDto);

                payment.changePaymentBySuccess("PAID", iamportResponse.getResponse().getImpUid());

                order.setOrderStatus("SUCCESS");
                order.setImpUid(impUid);
                orderRepository.save(order);

                payment.setOrderUid(order.getOrderUid());
                paymentRepository.save(payment); //결제 DB 저장

                //소콘북 저장
                AddMySoconRequest addMysoconRequest = AddMySoconRequest.builder()
                        .purchaseAt(LocalDateTime.now())
                        .expiredAt(LocalDateTime.now().plusDays(30))
                        .usedAt(null)
                        .status("unused")
                        .memberId(order.getMemberId())
                        .issueId(order.getIssueId())
                        .purchasedQuantity(order.getQuantity())
                        .build();
                log.info("addMysoconRequest: {}", addMysoconRequest);

                soconFeignClient.saveMySocon(addMysoconRequest); //feign을 통해 사용자의 쿠폰북에 저장

                return paymentDto;
            }
            orderRepository.updateOrderStatus(paymentCallbackRequestDto.getOrderUid(), "FAIL"); //Fail로 변경
            paymentDto.setStatus("CANCEL"); //결제 실패
            return paymentDto;

        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (PaymentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
