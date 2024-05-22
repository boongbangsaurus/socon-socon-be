package site.soconsocon.socon.store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@AllArgsConstructor
@Getter
public enum StoreErrorCode implements CustomError {

    // 400 BAD REQUEST
    TRANSACTION_FAIL(HttpStatus.BAD_REQUEST, "400 TRANSACTION_FAIL", "트랜잭션에 실패했습니다."),
    REDIS_TRANSACTION_FAIL(HttpStatus.BAD_REQUEST, "400 REDIS_TRANSACTION_FAIL", "트랜잭션에 실패했습니다."),
    INVALID_SOCON(HttpStatus.BAD_REQUEST, "400 INVALID_SOCON", "사용 불가능한 소콘입니다"),
    INVALID_ISSUE(HttpStatus.BAD_REQUEST, "400 INVALID_ISSUE", "발행 가능 상태가 아닙니다"),
    ALREADY_SAVED_STORE(HttpStatus.BAD_REQUEST, "400 ALREADY_SAVED_STORE", "이미 등록된 가게입니다"),
    ALREADY_SAVED_REGISTRATION_NUMBER(HttpStatus.BAD_REQUEST, "400 ALREADY_SAVED_REGISTRATION_NUMBER", "이미 등록된 사업자번호입니다"),
    ALREADY_SET_CLOSE_PLAN(HttpStatus.BAD_REQUEST, "400 ALREADY_SET_CLOSE_PLAN", "이미 폐업신고 된 가게입니다"),
    ISSUE_MAX_QUANTITY(HttpStatus.BAD_REQUEST, "400 ISSUE_MAX_QUANTITY", "발행 요청 가능 개수를 초과했습니다"),

    // 404 NOT FOUND
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "404 STORE_NOT_FOUND", "존재하지 않는 점포입니다"),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "404 ITEM_NOT_FOUND", "존재하지 않는 상품입니다"),
    ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "404 ISSUE_NOT_FOUND", "존재하지 않는 발행 정보입니다"),
    SOCON_NOT_FOUND(HttpStatus.NOT_FOUND, "404 SOCON_NOT_FOUND", "존재하지 않는 소콘입니다"),
    REGISTRATION_NUMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "404 REGISTRATION_NUMBER_NOT_FOUND", "존재하지 않는 사업자번호입니다"),

    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}
