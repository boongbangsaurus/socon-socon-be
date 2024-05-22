package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT i FROM ITEM i WHERE i.store.id = :storeId")
    List<Item> findItemsByStoreId(Integer storeId);

    @Query("SELECT i FROM ITEM i WHERE i.id = :itemId")
    Item findItemResponseByItemId(Integer itemId);

}
