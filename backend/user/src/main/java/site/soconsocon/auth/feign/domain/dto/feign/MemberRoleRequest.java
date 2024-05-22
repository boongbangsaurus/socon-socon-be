package site.soconsocon.auth.feign.domain.dto.feign;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberRoleRequest {

    private Integer memberId;

    private String role;
}
