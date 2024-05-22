package site.soconsocon.socon.global.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@AllArgsConstructor
@Getter
public enum ErrorCode implements CustomError {

    //사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404 MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다"),

    // 400 BAD REQUEST
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400 BAD_REQUEST", "잘못된 요청입니다"),
    BAD_REQUEST_VALUE(HttpStatus.BAD_REQUEST, "400 INVALID_VALUE", "요청 값이 잘못되었습니다"),

    // 403 FORBIDDEN
    FORBIDDEN(HttpStatus.FORBIDDEN, "403 FORBIDDEN", "해당 사용자에 허용되지 않는 요청입니다"),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500 INTERNAL_SERVER_ERROR", "서버 에러 발생"),
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}