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
public class BusinessHourRequest {

    private String day;

    @JsonProperty("is_working")
    private Boolean isWorking;

    @JsonProperty("open_at")
    private String openAt;

    @JsonProperty("close_at")
    private String closeAt;

    @JsonProperty("is_breaktime")
    private Boolean isBreaktime;

    @JsonProperty("breaktime_start")
    private String breaktimeStart;

    @JsonProperty("breaktime_end")
    private String breaktimeEnd;


}
