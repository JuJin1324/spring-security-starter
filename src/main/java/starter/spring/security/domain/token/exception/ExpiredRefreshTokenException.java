package starter.spring.security.domain.token.exception;

import starter.spring.security.exception.UnauthorizedException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public class ExpiredRefreshTokenException extends UnauthorizedException {
    public ExpiredRefreshTokenException() {
        super(null);
    }
}
