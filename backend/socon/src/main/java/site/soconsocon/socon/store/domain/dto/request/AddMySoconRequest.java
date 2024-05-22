package site.soconsocon.socon.store.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import site.soconsocon.socon.store.domain.entity.jpa.SoconStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddMySoconRequest {

    @JsonProperty("purchase_at")
    private LocalDateTime purchaseAt; //구매 일시

    @JsonProperty("expired_at")
    private LocalDateTime expiredAt; //만료 일시

    @JsonProperty("used_at")
    private LocalDateTime usedAt; //사용 일시

    private SoconStatus status; //상태 (unused, sogon, used, expired)

    @JsonProperty("member_id")
    private int memberId; //회원 번호

    @JsonProperty("issue_id")
    private int issueId; //발행 번호

    @JsonProperty("purchased_quantity")
    private int purchasedQuantity; //구매수량
}
