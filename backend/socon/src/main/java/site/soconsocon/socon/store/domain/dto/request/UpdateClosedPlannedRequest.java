package site.soconsocon.socon.store.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UpdateClosedPlannedRequest {

    @JsonProperty("close_after")
    private Integer closeAfter;

}
