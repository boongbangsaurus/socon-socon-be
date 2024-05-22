package site.soconsocon.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import site.soconsocon.auth.domain.entity.jpa.Member;
import site.soconsocon.auth.repository.MemberJpaRepository;

import java.util.Arrays;
import java.util.Optional;


/**
 * 현재 액세스 토큰으로 부터 인증된 유저의 상세정보(활성화 여부, 만료, 롤 등) 관련 서비스 정의.
 */
@Component
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberJpaRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> result = memberRepository.findById(Integer.parseInt(username));

        if (result.isPresent()) {
            Member member = result.get();
            MemberDetails userDetails = new MemberDetails(member);
            userDetails.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(String.valueOf(member.getRole()))));
            return userDetails;
        }
        return null;
    }
}
