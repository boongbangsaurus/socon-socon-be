package site.soconsocon.socon.sogon.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@AllArgsConstructor
@Getter
public enum SogonErrorCode implements CustomError {


    INVALID_SOGON(HttpStatus.BAD_REQUEST, "400 INVALID_SOGON", "게시 불가능한 소곤입니다"),
    SOGON_FAIL(HttpStatus.BAD_REQUEST, "400 SOGON_FAIL", "트랜잭션에 실패했습니다."),
    SOGON_NOT_FOUND(HttpStatus.NOT_FOUND, "404 SOGON_NOT_FOUND", "존재하지 않는 소곤입니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404 COMMENT_NOT_FOUND", "존재하지 않는 댓글입니다"),

    ;


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
