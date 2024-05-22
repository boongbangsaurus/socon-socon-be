package site.soconsocon.auth.domain.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberRegisterRequestDto {
    /**
     * 회원가입
     */
    private String email;

    private String password;

    private String name;

    private String nickname;

    private String phoneNumber;

    private boolean isAgreed;


}
