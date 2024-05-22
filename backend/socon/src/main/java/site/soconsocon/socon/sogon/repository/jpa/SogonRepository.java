package site.soconsocon.socon.sogon.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.sogon.domain.entity.jpa.Sogon;

import java.util.List;

public interface SogonRepository extends JpaRepository<Sogon, Integer> {

    @Query("select s from SOGON s where s.memberId = :memberId")
    List<Sogon> findAllByMemberId(Integer memberId);


    List<Sogon> findByLatBetweenAndLngBetween(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude);

    Sogon findBySoconId(Integer id);

    @Query("SELECT COUNT(*) FROM SOGON s WHERE s.memberId = :memberId")
    int getMySogonCount(Integer memberId);
}
