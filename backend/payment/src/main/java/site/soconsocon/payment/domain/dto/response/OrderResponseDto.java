package site.soconsocon.payment.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

    private int id;

    private String orderUid;

    private String itemName;

    private String orderStatus; //SUCCESS, FAIL

    private LocalDateTime orderTime; //주문 시간

    private int memberId;

    private int quantity;

}
