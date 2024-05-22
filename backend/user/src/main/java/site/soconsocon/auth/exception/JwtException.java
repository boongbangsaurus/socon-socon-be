package site.soconsocon.auth.exception;

import lombok.Getter;

@Getter
public class JwtException extends Exception {

    private final ErrorCode errorCode;

    public JwtException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
