//package site.soconsocon.auth.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//import site.soconsocon.auth.domain.entity.jpa.Member;
//
//import java.util.Optional;
//
//@Repository
//public interface MemberRepository1 extends JpaRepository<Member, Integer> {
//
//    Optional<Member> findMemberById(int memberId); //이메일로 유저찾기
//
//    Optional<Member> findMemberByEmail(String email); //이메일로 유저찾기
//
//    @Query("select m from Member m where m.nickname = :nickname")
//    Optional<Member> findMemberByNickname(String nickname); //닉네임으로 유저찾기
//
//    //소콘머니 충전
//    @Modifying
//    @Query("update Member m set m.soconMoney = m.soconMoney + :money where m.id = :memberId")
//    void chargeSoconMoney(int memberId, int money);
//
//    //소콘머니 출금
//    @Modifying
//    @Query("update Member m set m.soconMoney = m.soconMoney - :money where m.id = :memberId")
//    void withdrawSoconMoney(int memberId, int money);
//
//
//
//}
