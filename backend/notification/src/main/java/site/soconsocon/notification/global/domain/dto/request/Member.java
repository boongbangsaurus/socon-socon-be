package site.soconsocon.notification.global.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Member {
    private int memberId;

    private String email;

    private String nickname;

    private int soconMoney;

    private String soconPassword;
}
