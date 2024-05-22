package site.soconsocon.auth.domain.dto.request;


import lombok.Data;

@Data
public class WithdrawRequestDto {

    private int memberId; //점주의 PK

    private String accountNo;

    private int money;

    private String soconPassword;
}
