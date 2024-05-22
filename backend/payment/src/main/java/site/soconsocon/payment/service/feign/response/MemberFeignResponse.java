package site.soconsocon.payment.service.feign.response;

import lombok.Data;

@Data
public class MemberFeignResponse {

    private int memberId;

    private String email;

    private String nickname;

    private int soconMoney;

    private String soconPassword;

}
