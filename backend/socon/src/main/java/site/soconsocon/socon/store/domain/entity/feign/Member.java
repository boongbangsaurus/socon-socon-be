package site.soconsocon.socon.store.domain.entity.feign;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Member {

        private int memberId;

        private String email;

        private String nickname;

        private String name;

        private String phoneNumber;

        private String profileUrl;

        private String account;

        private int soconMoney;

        private String soconPassword;

}
