package site.soconsocon.auth.domain.dto.response;

import lombok.Data;

@Data
public class MemberFeignResponse {

    private int memberId;

    private String email;

    private String nickname;

    private String name;

    private String phoneNumber;

    private String profileUrl;

    private String account;

    private int soconMoney;

    private String soconPassword;

}
