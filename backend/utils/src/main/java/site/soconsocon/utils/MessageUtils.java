package site.soconsocon.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import site.soconsocon.utils.exception.CustomError;

import java.io.IOException;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageUtils<T> {
    private final DataHeader dataHeader;
    private final T dataBody;
    public static <T> MessageUtils<T> success(T dataBody) {
        return MessageUtils.<T>builder()
                .dataHeader(DataHeader.success())
                .dataBody(dataBody)
                .build();
    }

    public static <T> MessageUtils<T> success(T dataBody, String code, String resultMessage) {
        return MessageUtils.<T>builder()
                .dataHeader(DataHeader.success(code, resultMessage))
                .dataBody(dataBody)
                .build();
    }

    public static MessageUtils success() {
        return MessageUtils.builder()
                .dataHeader(DataHeader.noContentSuccess())
                .build();
    }

    public static <T> MessageUtils<T> fail(String resultCode, String resultMessage) {
        return MessageUtils.<T>builder()
                .dataHeader(DataHeader.fail(resultCode, resultMessage))
                .dataBody(null)
                .build();
    }

    public static void customExceptionHandler(HttpServletResponse response, CustomError errorCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(MessageUtils.fail(errorCode.getErrorCode(),errorCode.getMessage())));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private static class DataHeader {

        private int successCode;
        private String resultCode;
        private String resultMessage;

        private static DataHeader noContentSuccess() {
            return DataHeader.builder()
                    .successCode(0)
                    .resultCode("204 NO_CONTENT")
                    .build();
        }

        private static DataHeader success() {
            return DataHeader.builder()
                    .successCode(0)
                    .resultCode("200 OK")
                    .build();
        }



        private static DataHeader success(String code, String resultMessage) {
            return DataHeader.builder()
                    .successCode(0)
                    .resultCode(code)
                    .resultMessage(resultMessage)
                    .build();
        }

        private static DataHeader fail(String resultCode, String resultMessage) {
            return DataHeader.builder()
                    .successCode(1)
                    .resultCode(resultCode)
                    .resultMessage(resultMessage)
                    .build();
        }
    }
}

