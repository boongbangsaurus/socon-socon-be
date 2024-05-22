package site.soconsocon.socon.sogon.exception;

import lombok.Getter;

@Getter
public class SogonException extends RuntimeException{

    private final SogonErrorCode errorCode;

    public SogonException(SogonErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
