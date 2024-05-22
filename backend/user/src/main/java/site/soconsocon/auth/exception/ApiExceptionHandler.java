package site.soconsocon.auth.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.soconsocon.utils.MessageUtils;

@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity memberExceptionHandler(MemberException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity accountExceptionHandler(AccountException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));
    }
}
