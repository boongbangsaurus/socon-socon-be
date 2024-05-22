package site.soconsocon.auth.feign.domain.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * private Integer memberId;
 * private String token;
 * private String deviceType;
 */
@Data
@AllArgsConstructor
public class SaveTokenRequest {
    private Integer memberId;
    private String token;
    private String deviceType;
}
