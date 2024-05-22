package site.soconsocon.socon.search.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@Getter
@AllArgsConstructor
public enum SearchErrorCode implements CustomError {
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "400", "잘못된 요청 형식입니다."),
    SAVE_DOCUMENT_FAIL(HttpStatus.BAD_REQUEST, "400", "도큐먼트 추가에 실패했습니다."),
    SEARCH_FAIL(HttpStatus.BAD_REQUEST, "400", "검색에 실패했습니다.");

    private HttpStatus httpStatus;
    private String errorCode;
    private String message;

}