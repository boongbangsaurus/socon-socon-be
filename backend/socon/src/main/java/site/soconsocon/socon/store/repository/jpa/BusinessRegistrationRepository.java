package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.BusinessRegistration;

import java.util.List;

public interface BusinessRegistrationRepository extends JpaRepository<BusinessRegistration, Integer> {


    @Query("SELECT b FROM BUSINESS_REGISTRATION b WHERE b.memberId = :memberId")
    List<BusinessRegistration> getBusinessNumberList(int memberId);
}
