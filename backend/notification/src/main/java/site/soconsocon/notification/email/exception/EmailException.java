package site.soconsocon.notification.email.exception;
import lombok.Getter;
import site.soconsocon.notification.fcm.exception.FcmErrorCode;

@Getter
public class EmailException extends RuntimeException {
    private final EmailErrorCode errorCode;

    public EmailException(EmailErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}