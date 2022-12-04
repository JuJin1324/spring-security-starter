package starter.springsecurity.web.exception;

import starter.springsecurity.exception.UnauthorizedException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public class InvalidJsonWebTokenException extends UnauthorizedException {

    public InvalidJsonWebTokenException(String message) {
        super(message);
    }
}
