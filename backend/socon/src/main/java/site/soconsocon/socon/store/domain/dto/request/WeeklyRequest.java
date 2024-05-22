package site.soconsocon.socon.store.domain.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class WeeklyRequest {

    private int year;
    private int month;
    private int week;
}
