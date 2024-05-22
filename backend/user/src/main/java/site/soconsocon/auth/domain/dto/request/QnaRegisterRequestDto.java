package site.soconsocon.auth.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QnaRegisterRequestDto {
    /**
     * 문의 등록
     */

    private String title;

    private String content;

    private int memberId;

}
