package site.soconsocon.payment.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;
import site.soconsocon.payment.domain.dto.response.PaymentResponseDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer id;

    @Column(nullable = false)
    private int amount; //결제 금액

    private String status; //결제 상태

    private String impUid; //결제 고유번호

    private String itemName; //상품명

    private int cancelPrice; //취소 금액

    private String orderUid; //주문 고유번호 id

    public Payment(PaymentResponseDto dto) {
        this.impUid = dto.getImpUid();
        this.amount = dto.getAmount();
        this.itemName = dto.getItemName();
        this.status = dto.getStatus();

    }
    public void changePaymentBySuccess(String status, String paymentUid) {
        this.status = status;
        this.impUid = paymentUid;
    }

}
