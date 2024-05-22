package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.BusinessHour;

import java.util.List;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Integer> {

    @Query("SELECT b FROM BUSINESS_HOUR b WHERE b.store.id = :storeId")
    List<BusinessHour> findBusinessHourResponseByStoreId(Integer storeId);


    List<BusinessHour> findByStoreId(Integer storeId);
}
