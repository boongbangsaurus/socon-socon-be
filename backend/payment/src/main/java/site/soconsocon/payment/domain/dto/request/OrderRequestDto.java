package site.soconsocon.payment.domain.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequestDto {

    private String itemName;

    private int price;

    private int quantity; //주문 수량

    private int issueId; // 발행 아이디


}
