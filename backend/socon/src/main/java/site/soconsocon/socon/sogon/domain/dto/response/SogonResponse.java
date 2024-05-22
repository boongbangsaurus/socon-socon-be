package site.soconsocon.socon.sogon.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SogonResponse {

    private Integer id;
    private String title;
    private String memberName;
    private String memberImg;
    private String content;
    private String image1;
    private String image2;
    private String soconImg;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private boolean isExpired;
}
