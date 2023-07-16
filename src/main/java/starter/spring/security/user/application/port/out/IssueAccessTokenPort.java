package starter.spring.security.user.application.port.out;

import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.IssuedAccessToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface IssueAccessTokenPort {
    IssuedAccessToken issueNewOne(UserId userId);
}
