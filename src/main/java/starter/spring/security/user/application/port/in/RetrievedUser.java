package starter.spring.security.user.application.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import starter.spring.security.user.application.domain.Email;
import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RequiredArgsConstructor
@Getter
public class RetrievedUser {
    private final UserId id;
    private final String fullName;
    private final Email email;
}
