package site.soconsocon.auth.domain.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberResponseDto {

    private String email;

    private String nickname;

    private String name;

    private String phoneNumber;

    private int soconMoney;

    private String soconPassword;

    private String profileUrl;

    private String account;
}
