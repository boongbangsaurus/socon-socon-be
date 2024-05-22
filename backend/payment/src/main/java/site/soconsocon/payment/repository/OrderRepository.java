package site.soconsocon.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.soconsocon.payment.domain.entity.jpa.Orders;
import site.soconsocon.payment.domain.entity.jpa.Payment;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {

    @Query("select o from Orders o where o.orderUid = :orderUid")
    Optional<Orders> findOrderByOrderUid(@Param("orderUid") String orderUid);

    Optional<Orders> findOrderByImpUid(@Param("impUid") String impUid);

    @Query("update Orders o set o.orderStatus = :orderStatus where o.orderUid = :orderUid")
    void updateOrderStatus(String orderUid, String orderStatus);

}
