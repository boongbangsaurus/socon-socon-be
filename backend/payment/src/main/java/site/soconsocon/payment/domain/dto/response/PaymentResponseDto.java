package site.soconsocon.payment.domain.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDto {

    private String impUid; //결제 고유번호

    private String itemName;

    private String status;

    private int amount;
}
