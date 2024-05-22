package site.soconsocon.auth.domain.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MyPageResponseDto {

    private String email;

    private String nickname;

    private String name;

    private String phoneNumber;

    private int soconMoney;

    private String soconPassword;

    private String profileUrl;

    private String account;

    private int soconCnt; //보유 소콘

    private int sogonCnt; //작성 소곤

    private int sogonReplyCnt; //댓글 소곤

}
