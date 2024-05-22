package site.soconsocon.socon.search.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreNearMe {
    Double lat;
    Double lng;
}
