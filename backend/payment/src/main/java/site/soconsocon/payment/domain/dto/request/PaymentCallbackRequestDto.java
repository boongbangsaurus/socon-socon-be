package site.soconsocon.payment.domain.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCallbackRequestDto {
    private String impUid; //결제 고유번호

    private String orderUid; //주문 고유번호
}
