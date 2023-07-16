package starter.spring.security.user.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import starter.spring.security.user.application.domain.Email;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@AllArgsConstructor
@Getter
public class SignUpUserCommand {
    private String fullName;
    private Email email;
    private String password;
}
