package site.soconsocon.socon.search.exception;

import lombok.Getter;

@Getter
public class SearchException extends RuntimeException{
    private final SearchErrorCode errorCode;

    public SearchException(SearchErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
