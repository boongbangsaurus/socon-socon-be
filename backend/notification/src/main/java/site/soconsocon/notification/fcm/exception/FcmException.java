package site.soconsocon.notification.fcm.exception;
import lombok.Getter;

import java.io.IOException;

@Getter
public class FcmException extends RuntimeException {
    private final FcmErrorCode errorCode;

    public FcmException(FcmErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}