package site.soconsocon.notification.fcm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.soconsocon.utils.exception.CustomError;

@Getter
@AllArgsConstructor
public enum FcmErrorCode implements CustomError {
    SUBSCRIBE_FAIL(HttpStatus.BAD_REQUEST,"토픽 구독에 실패했습니다.", "400"),
    CREATE_TOKEN_FAIL(HttpStatus.BAD_REQUEST,"토큰 생성에 실패했습니다.", "400"),
    CAN_NOT_SEND_NOTIFICATION(HttpStatus.BAD_REQUEST,"푸시 알림 전송에 실패했습니다.", "400"),
    NO_EXIST_TARGET( HttpStatus.BAD_REQUEST,"푸시 알림 전송에 실패했습니다.","400"),
    NO_EXIST_TOKEN(HttpStatus.BAD_REQUEST,"본인 디바이스 토큰이 존재하지 않습니다.", "400"),
    SAVING_TOKEN_FAIL(HttpStatus.BAD_REQUEST,"디바이스 토큰 저장에 실패했습니다.", "400");

    private HttpStatus httpStatus;
    private final String message;
    private String errorCode;
}
