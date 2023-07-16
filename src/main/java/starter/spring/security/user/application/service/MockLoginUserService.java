package starter.spring.security.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import starter.spring.security.user.application.domain.User;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.LoginUserCommand;
import starter.spring.security.user.application.port.in.LoginUserUseCase;
import starter.spring.security.user.application.port.out.IssueAccessTokenPort;
import starter.spring.security.user.application.port.in.IssuedAccessToken;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Service
@RequiredArgsConstructor
public class MockLoginUserService implements LoginUserUseCase {
    private final IssueAccessTokenPort issueAccessTokenPort;

    @Override
    public IssuedAccessToken login(LoginUserCommand command) {
        // TODO: LoadUserPort
        User user = new User(new UserId(UUID.randomUUID()), "이름 mock", command.getEmail(), command.getPassword());
        return issueAccessTokenPort.issueNewOne(user.getId());
    }
}
