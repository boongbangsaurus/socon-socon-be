package site.soconsocon.auth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import site.soconsocon.auth.domain.entity.jpa.Member;

import static site.soconsocon.auth.domain.entity.jpa.QMember.member;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Member> findMemberById(int memberId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(member.id.eq(memberId))
                .fetchOne());
    }


    public Optional<Member> findMemberByEmail(String email) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne());
    }


    public Optional<Member> findMemberByNickname(String nickname) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
                .fetchOne());
    }


    public void chargeSoconMoney(int memberId, int money) {
        jpaQueryFactory
                .update(member)
                .set(member.soconMoney, member.soconMoney.add(money))
                .where(member.id.eq(memberId))
                .execute();
    }


    public void withdrawSoconMoney(int memberId, int money) {
        jpaQueryFactory
                .update(member)
                .set(member.soconMoney, member.soconMoney.subtract(money))
                .where(member.id.eq(memberId))
                .execute();
    }
}
