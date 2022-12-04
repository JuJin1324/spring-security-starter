package starter.springsecurity.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/07
 */

@Getter
public class ErrorResponse {
    private long   timestamp;
    private int    status;
    private String error;
    private String message;

    private ErrorResponse(HttpStatus status, String message) {

        this.timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        this.status = status.value();
        this.error = status.name();
        this.message = message;
    }

    public static ErrorResponse of(RuntimeException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
