package site.soconsocon.socon.store.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UpdateStoreInfoRequest {

    private String image;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    private Double lat;
    private Double lng;
    private String introduction;

    @JsonProperty("business_hour")
    private List<BusinessHourRequest> businessHour;

}
