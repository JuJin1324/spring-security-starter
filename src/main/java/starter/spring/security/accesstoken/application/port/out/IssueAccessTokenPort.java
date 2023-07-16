package starter.spring.security.accesstoken.application.port.out;

import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface IssueAccessTokenPort {
    AccessToken issueNewOne(UserId userId);
}
