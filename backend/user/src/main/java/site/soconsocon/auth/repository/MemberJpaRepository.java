package site.soconsocon.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.soconsocon.auth.domain.entity.jpa.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Integer> {

}
