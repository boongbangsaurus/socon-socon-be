package site.soconsocon.auth.exception;

import lombok.Getter;

@Getter
public class AccountException extends Exception {

    private final ErrorCode errorCode;

    public AccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
