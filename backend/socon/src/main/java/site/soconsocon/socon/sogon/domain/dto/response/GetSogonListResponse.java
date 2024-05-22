package site.soconsocon.socon.sogon.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetSogonListResponse {

    private Integer id;
    private String title;
    private double lat;
    private double lng;
    private Integer lastTime;
    private String memberName;
    private Integer commentCount;
    private String soconImg;
    private Boolean isPicked;
}
