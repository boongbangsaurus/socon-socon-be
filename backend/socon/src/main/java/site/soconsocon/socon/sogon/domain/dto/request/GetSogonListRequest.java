package site.soconsocon.socon.sogon.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetSogonListRequest {

    private Double lat;
    private Double lng;

}
