package site.soconsocon.socon.global.exception;

import lombok.Getter;
import site.soconsocon.socon.global.domain.ErrorCode;

@Getter
public class SoconException extends RuntimeException{

    private final ErrorCode errorCode;

    public SoconException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
