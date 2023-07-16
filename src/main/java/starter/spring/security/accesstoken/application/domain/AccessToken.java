package starter.spring.security.accesstoken.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RequiredArgsConstructor
@Getter
public class AccessToken {
    private final String value;
    private final UserId userId;
}
