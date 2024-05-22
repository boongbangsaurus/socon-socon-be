package site.soconsocon.socon.store.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class AddIssueRequest {

    @JsonProperty("is_main")
    private Boolean isMain;

    @JsonProperty("is_discounted")
    private Boolean isDiscounted;

    @JsonProperty("discounted_price")
    private Integer discountedPrice;

    @JsonProperty("max_quantity")
    private Integer maxQuantity;

    private Integer period;

}
