package site.soconsocon.auth.domain.dto.request;

import lombok.Data;

/**
 * 입금
 */
@Data
public class DepositRequestDto {

    private int memberId; //점주의 PK

    private int money;
}
