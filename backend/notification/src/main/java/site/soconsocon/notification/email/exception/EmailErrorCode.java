package site.soconsocon.notification.email.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@Getter
@AllArgsConstructor
public enum EmailErrorCode implements CustomError {
    DIFFERENT_NUMBER(HttpStatus.BAD_REQUEST,"번호가 다릅니다.","400"),
    WRONG_CODE(HttpStatus.BAD_REQUEST,"잘못된 코드입니다.","400"),
    SEND_ERROR(HttpStatus.BAD_REQUEST,"이메일 전송에 실패했습니다.","400"),
    ALREADY_JOIN_EMAIL(HttpStatus.BAD_REQUEST,"이미 가입된 메일입니다.", "400");

    private HttpStatus httpStatus;
    private final String message;
    private String errorCode;
}
