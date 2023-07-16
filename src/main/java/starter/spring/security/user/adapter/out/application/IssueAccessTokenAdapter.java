package starter.spring.security.user.adapter.out.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.in.IssueAccessTokenUseCase;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.out.IssueAccessTokenPort;
import starter.spring.security.user.application.port.in.IssuedAccessToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Component
@RequiredArgsConstructor
public class IssueAccessTokenAdapter implements IssueAccessTokenPort {
    private final IssueAccessTokenUseCase issueAccessTokenUseCase;

    @Override
    public IssuedAccessToken issueNewOne(UserId id) {
        AccessToken accessToken = issueAccessTokenUseCase.issueNewOne(id);
        return new IssuedAccessToken(accessToken.getValue());
    }
}
