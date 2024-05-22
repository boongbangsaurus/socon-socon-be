package site.soconsocon.auth.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 로그인 API ([POST] /api/v1/members/auth) 요청에 대한 응답값 정의.
 */
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberLoginResponseDto {

    String accessToken;
    String refreshToken;
    String nickname;


}
