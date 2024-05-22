package site.soconsocon.payment.service.feign.response;

import lombok.Data;

@Data
public class OrderFeignResponse {

    private int name; //상품명

    private int orderId; //주문 PK

    private String orderUid; //주문 고유번호

    private int quantity; //개수

    private int price; //가격

    private int paymentId; //결제 PK

    private String paymentUid; //결제 고유번호

    private int issueId; //발행 PK

    private int memberId; //회원 PK
}
