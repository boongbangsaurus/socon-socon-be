package site.soconsocon.auth.domain.dto.request;

import lombok.Data;

@Data
public class AccountNoRequestDto {

    private String accountNo; //계좌번호

    private String soconPassword; //소콘비밀번호
}
