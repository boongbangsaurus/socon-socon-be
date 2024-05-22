package site.soconsocon.payment.domain.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentRequestDto {

    private String orderUid;

    private String itemName;

    private String buyerName;

    private int paymentPrice;

    private String paymentStatus;

    @Builder
    public PaymentRequestDto(String orderUid, String itemName, String buyerName, int paymentPrice) {
        this.orderUid = orderUid;
        this.itemName = itemName;
        this.buyerName = buyerName;
        this.paymentPrice = paymentPrice;
    }
}
