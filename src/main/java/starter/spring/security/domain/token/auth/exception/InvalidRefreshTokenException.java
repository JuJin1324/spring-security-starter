package starter.spring.security.domain.token.auth.exception;

import starter.spring.security.exception.UnauthorizedException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public class InvalidRefreshTokenException extends UnauthorizedException {

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
