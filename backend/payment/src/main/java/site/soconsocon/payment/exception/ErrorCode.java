package site.soconsocon.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@AllArgsConstructor
@Getter
public enum ErrorCode implements CustomError {

    //주문
    ORDER_FAIL(HttpStatus.BAD_REQUEST, "400", "주문 실패하였습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "주문 내역이 없습니다"),


    //결제
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "결제 내역이 없습니다"),
    PAYMENT_FAIL(HttpStatus.BAD_REQUEST, "400", "주문 실패하였습니다."),
    PAYMENT_AMOUNT_NOT_SAME(HttpStatus.BAD_REQUEST, "400", "결제 금액이 맞지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}
