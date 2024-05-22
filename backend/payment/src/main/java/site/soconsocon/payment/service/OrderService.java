package site.soconsocon.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.soconsocon.payment.domain.dto.request.OrderRequestDto;
import site.soconsocon.payment.domain.dto.response.OrderRegisterResponseDto;
import site.soconsocon.payment.domain.dto.response.OrderResponseDto;
import site.soconsocon.payment.domain.dto.response.PaymentByOrderResponseDto;
import site.soconsocon.payment.domain.entity.jpa.Orders;
import site.soconsocon.payment.domain.entity.jpa.Payment;
import site.soconsocon.payment.exception.ErrorCode;
import site.soconsocon.payment.exception.PaymentException;
import site.soconsocon.payment.repository.OrderRepository;
import site.soconsocon.payment.repository.PaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public OrderRegisterResponseDto saveOrder(int memberId, OrderRequestDto orderRequestDto) {

        Orders order = Orders.builder()
                .orderUid(UUID.randomUUID().toString())
                .itemName(orderRequestDto.getItemName())
                .price(orderRequestDto.getPrice())
                .memberId(memberId)
                .quantity(orderRequestDto.getQuantity())
                .issueId(orderRequestDto.getIssueId())
                .build();

        orderRepository.save(order);
        String orderUid = order.getOrderUid();
        OrderRegisterResponseDto orderRegisterResponseDto = OrderRegisterResponseDto.builder().orderUid(orderUid).build();

        return orderRegisterResponseDto;
    }

    public PaymentByOrderResponseDto findOrderByImpUid(String impUid) throws PaymentException {
        //결제 정보 가져오기
        Payment payment = paymentRepository.findPaymentByImpUid(impUid).orElseThrow(
                () -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        //주문내역 조회
        Orders order = orderRepository.findOrderByImpUid(impUid)
                .orElseThrow(() -> new PaymentException(ErrorCode.ORDER_NOT_FOUND));

        PaymentByOrderResponseDto paymentByOrderResponseDto = PaymentByOrderResponseDto.builder()
                .id(payment.getId())
                .impUid(payment.getImpUid())
                .amount(payment.getAmount())
                .orderUid(payment.getOrderUid())
                .itemName(payment.getItemName())
                .build();

        return paymentByOrderResponseDto;
    }

    public OrderResponseDto findOrderByOrderId(String orderId) throws PaymentException {
        //주문내역 조회
        Orders order = orderRepository.findOrderByOrderUid(orderId)
                .orElseThrow(() -> new PaymentException(ErrorCode.ORDER_NOT_FOUND));

        OrderResponseDto orderResponseDto = OrderResponseDto.builder()
                .id(order.getId())
                .orderUid(order.getOrderUid())
                .itemName(order.getItemName())
                .orderStatus(order.getOrderStatus())
                .memberId(order.getMemberId())
                .quantity(order.getQuantity())
                .build();

        return orderResponseDto;
    }


}
