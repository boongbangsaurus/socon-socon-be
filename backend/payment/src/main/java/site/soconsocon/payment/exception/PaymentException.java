package site.soconsocon.payment.exception;

import lombok.Getter;

@Getter
public class PaymentException extends Exception {

    private final ErrorCode errorCode;

    public PaymentException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
