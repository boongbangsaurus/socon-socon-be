package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.FavStore;

import java.util.List;
import java.util.Optional;

public interface FavStoreRepository extends JpaRepository<FavStore, Integer> {

    @Query("SELECT COUNT(*) FROM FAV_STORE s WHERE s.storeId = :storeId")
    Integer countByStoreId(Integer storeId);


    @Query("SELECT s FROM FAV_STORE s WHERE s.memberId = :memberId AND s.storeId = :storeId")
    FavStore findByMemberIdAndStoreId(Integer memberId, Integer storeId);


    @Query("SELECT s FROM FAV_STORE s WHERE s.memberId = :memberId")
    List<FavStore> findByMemberId(Integer memberId);

    @Query("DELETE FROM FAV_STORE s WHERE s.storeId = :id")
    void deleteByStoreId(Integer id);
}
