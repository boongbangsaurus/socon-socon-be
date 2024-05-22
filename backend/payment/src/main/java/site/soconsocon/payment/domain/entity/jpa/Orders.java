package site.soconsocon.payment.domain.entity.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer id;

    private String orderUid; //주문 고유번호

    private String itemName;

    private int price;

    private String orderStatus;

    private LocalDateTime orderTime; //주문 시간

    private int memberId; //회원 아이디

    private int issueId; // 발행 아이디

    private String impUid; //결제 고유번호

    private int quantity; //주문 수량

}
