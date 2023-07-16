package starter.spring.security.accesstoken.application.port.in;

import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.out.ExpiredAccessTokenException;
import starter.spring.security.accesstoken.application.port.out.InvalidAccessTokenException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface ParseAccessTokenUseCase {
    AccessToken parse(String token) throws ExpiredAccessTokenException, InvalidAccessTokenException;
}
