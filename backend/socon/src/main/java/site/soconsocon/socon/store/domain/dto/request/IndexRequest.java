package site.soconsocon.socon.store.domain.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class IndexRequest {

    private int year;
    private int month;
    private int index;
}
