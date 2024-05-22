package site.soconsocon.socon.store.domain.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RoleRequest {

    private Integer memberId;
    private String role;
}
