package site.soconsocon.socon.store.domain.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class StoreAnalysisRequest {

    private int year;
    private int month;
}
