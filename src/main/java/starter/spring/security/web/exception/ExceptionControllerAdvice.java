package starter.spring.security.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/07
 */

@RestControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * 개발자가 미처 체크하지 못한 예외 발생
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(HttpServletRequest request, RuntimeException ex) {
        return ErrorResponse.of(ex);
    }
}
