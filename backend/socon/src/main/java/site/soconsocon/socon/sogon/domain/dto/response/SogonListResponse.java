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
public class SogonListResponse {

    private Integer id;
    private String title;
    private String soconImg;
    private LocalDateTime createdAt;
    private Boolean isExpired;
    private boolean isPicked;

}
