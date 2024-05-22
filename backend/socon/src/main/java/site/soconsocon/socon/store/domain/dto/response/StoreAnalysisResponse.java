package site.soconsocon.socon.store.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreAnalysisResponse {


    private String storeName;

    private int year;

    private int month;

    private int nowTotal;

    private int lastTotal;

    private int opendYear;

    private int opendMonth;

    private List<SalesAnalysisResponse> section1;

    private int section2IndexTotal;

    private List<SalesAnalysisResponse> section2;

    private int section3IndexTotal;

    private List<WeeklyAnalysisResponse> section3;

    private int section4IndexTotal;

    private List<IssuedAnalysisListResponse> section4;



}
