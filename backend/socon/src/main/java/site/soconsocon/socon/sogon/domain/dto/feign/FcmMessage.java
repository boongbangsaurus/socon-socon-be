package site.soconsocon.socon.sogon.domain.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FcmMessage {
    private String title;
    private String body;
    private Integer memberId;
    private Long topicId;
}
