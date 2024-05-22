package site.soconsocon.socon.store.domain.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IssueListResponse {

    private Integer id;
    private Boolean isMain;
    private String name;
    private String image;
    private Integer issuedQuantity;
    private Integer leftQuantity;
    private Boolean isDiscounted;
    private Integer price;
    private Integer discountedPrice;
    private LocalDate createdAt;
}
