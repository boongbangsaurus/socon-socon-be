package site.soconsocon.socon.search.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * int storeId;
 * String name;
 * String imageUrl;
 * String address;
 * String category;
 * String createdAt;
 * bool isLike;
 * String mainSocon;
 * int distance;
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FoundStoreInfo {
    private Integer storeId;
    private String name;
    private String imageUrl;
    private String address;
    private String category;
    private Boolean isLike;
    private String mainSocon;
    private Integer distance;
}
