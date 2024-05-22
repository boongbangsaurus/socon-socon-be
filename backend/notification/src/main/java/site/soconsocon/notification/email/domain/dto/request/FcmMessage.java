package site.soconsocon.notification.email.domain.dto.request;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FcmMessage {
    private String title;
    private String body;
    private String targetToken;
    private String topicName;
}