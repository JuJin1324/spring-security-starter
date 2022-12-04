package starter.springsecurity.domain.token.registration.exception;

import starter.springsecurity.exception.UnauthorizedException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public class InvalidRegistrationTokenException extends UnauthorizedException {
    public InvalidRegistrationTokenException() {
        super("Invalid registration token");
    }
}
