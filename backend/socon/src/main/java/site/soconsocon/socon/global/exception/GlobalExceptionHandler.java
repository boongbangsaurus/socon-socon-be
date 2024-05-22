package site.soconsocon.socon.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.soconsocon.socon.search.exception.SearchException;
import site.soconsocon.socon.sogon.exception.SogonException;
import site.soconsocon.socon.store.exception.StoreException;
import site.soconsocon.utils.MessageUtils;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(StoreException.class)
    public ResponseEntity<Object> StoreExceptionHandler(StoreException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));

    }

    @ExceptionHandler(SogonException.class)
    public ResponseEntity<Object> SogonExceptionHandler(SogonException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));

    }

    @ExceptionHandler(SoconException.class)
    public ResponseEntity<Object> SearchExceptionHandler(SoconException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));

    }

    @ExceptionHandler(SearchException.class)
    public ResponseEntity<Object> SearchExceptionHandler(SearchException e) {
        log.debug(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(MessageUtils.fail(String.valueOf(e.getErrorCode()), e.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception e) {
        log.error("An unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageUtils.fail("INTERNAL_SERVER_ERROR", "An unexpected error occurred"));
    }

}
