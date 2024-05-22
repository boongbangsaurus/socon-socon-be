package site.soconsocon.auth.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class MemberLoginRequestDto {

    private String email;

    private String password;

    private String fcmToken;

    @Builder
    public MemberLoginRequestDto(String email, String password, String fcmToken) {
        this.email = email;
        this.password = password;
        this.fcmToken = fcmToken;
    }
}
