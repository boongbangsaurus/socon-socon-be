package site.soconsocon.auth.feign.domain.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPageSoconResponse {

    private int soconCnt; //보유 소콘

    private int sogonCnt; //작성 소곤

    private int sogonReplyCnt; //댓글 소곤
}
