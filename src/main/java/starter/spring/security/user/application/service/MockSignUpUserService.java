package starter.spring.security.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import starter.spring.security.user.application.domain.User;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.SignUpUserCommand;
import starter.spring.security.user.application.port.in.SignUpUserUseCase;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Service
@RequiredArgsConstructor
public class MockSignUpUserService implements SignUpUserUseCase {

    @Override
    public UserId singUp(SignUpUserCommand command) {
        User user = new User(new UserId(UUID.randomUUID()),
                command.getFullName(), command.getEmail(), command.getPassword());
        return user.getId();
    }
}
