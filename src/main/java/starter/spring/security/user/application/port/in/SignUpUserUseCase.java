package starter.spring.security.user.application.port.in;

import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
public interface SignUpUserUseCase {
    UserId singUp(SignUpUserCommand command);
}
