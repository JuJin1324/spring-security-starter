package starter.spring.security.accesstoken.application.port.out;

import starter.spring.security.accesstoken.application.domain.AccessToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface ParseAccessTokenPort {
    AccessToken parse(String token);
}
