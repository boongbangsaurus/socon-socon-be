package site.soconsocon.notification.fcm.domain.dto.request;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FcmMessage {
    private String title;
    private String body;
    private Integer memberId;
    private Long topicId;
}
