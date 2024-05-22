package site.soconsocon.payment.domain.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentByOrderResponseDto {

    private int id;

    private String impUid; //결제 고유번호

    private int amount;

    private String orderUid; //주문 고유번호

    private String itemName;


}
