package site.soconsocon.socon.sogon.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddSogonRequest {

    private String title;
    private String content;
    private String image1;
    private String image2;
    private Double lat;
    private Double lng;

    @JsonProperty("socon_id")
    private Integer soconId;

}
