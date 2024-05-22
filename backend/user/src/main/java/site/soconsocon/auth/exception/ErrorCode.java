package site.soconsocon.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@AllArgsConstructor
@Getter
public enum ErrorCode implements CustomError {

    //필터 검증 에러

    //사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "사용자를 찾을 수 없습니다."),
    DUPLE_EMAIL(HttpStatus.BAD_REQUEST, "400", "이미 존재하는 이메일입니다."),
    DUPLE_NICK(HttpStatus.BAD_REQUEST, "400", "중복된 닉네임 입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "400", "비밀번호가 틀렸습니다."),

    //FCM
//    NOT_FOUND_FCM_TOKEN(HttpStatus.NOT_FOUND, "404", "FCM 토큰이 없습니다"),

    //소콘머니
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "400", "소콘 비밀번호를 잘못 입력했습니다."),
    NO_MONEY(HttpStatus.BAD_REQUEST, "400", "잔액이 모자릅니다."),
    DEPOSIT_FAIL(HttpStatus.BAD_REQUEST, "400", "충전이 실패했습니다."),
    WITHDRAW_FAIL(HttpStatus.BAD_REQUEST, "400", "출금이 실패했습니다."),
    ACCOUNT_REGISTER_FAIL(HttpStatus.BAD_REQUEST, "400", "계좌번호 등록에 실패하였습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}
