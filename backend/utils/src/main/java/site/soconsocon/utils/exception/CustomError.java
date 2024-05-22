package site.soconsocon.utils.exception;

import org.springframework.http.HttpStatus;

public interface CustomError {
    HttpStatus getHttpStatus();
    String getMessage();
    String getErrorCode();

}
