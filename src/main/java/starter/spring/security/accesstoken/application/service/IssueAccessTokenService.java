package starter.spring.security.accesstoken.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.in.IssueAccessTokenUseCase;
import starter.spring.security.accesstoken.application.port.out.IssueAccessTokenPort;
import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Service
@RequiredArgsConstructor
public class IssueAccessTokenService implements IssueAccessTokenUseCase {
    private final IssueAccessTokenPort issueAccessTokenPort;

    @Override
    public AccessToken issueNewOne(UserId id) {
        // TODO: userId 에 해당하는 회원이 있는지 조회
        return issueAccessTokenPort.issueNewOne(id);
    }
}
