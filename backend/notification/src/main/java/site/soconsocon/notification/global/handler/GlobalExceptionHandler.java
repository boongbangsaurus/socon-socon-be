package site.soconsocon.notification.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.soconsocon.notification.email.exception.EmailErrorCode;
import site.soconsocon.notification.email.exception.EmailException;
import site.soconsocon.notification.fcm.exception.FcmErrorCode;
import site.soconsocon.notification.fcm.exception.FcmException;
import site.soconsocon.utils.MessageUtils;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(FcmException.class)
    public ResponseEntity memberExceptionHandler(FcmException e) {
        FcmErrorCode code = e.getErrorCode();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }
    @ExceptionHandler(EmailException.class)
    public ResponseEntity memberExceptionHandler(EmailException e) {
        EmailErrorCode code = e.getErrorCode();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }
}
