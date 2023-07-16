package starter.spring.security.user.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import starter.spring.security.user.application.domain.Email;
import starter.spring.security.user.application.port.in.IssuedAccessToken;
import starter.spring.security.user.application.port.in.LoginUserCommand;
import starter.spring.security.user.application.port.in.LoginUserUseCase;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RestController
@RequiredArgsConstructor
public class LoginUserController {
    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("/users/login")
    public IssuedAccessTokenResource login(@RequestBody LoginUserRequest request) {
        LoginUserCommand command = new LoginUserCommand(new Email(request.getEmailAddress()), request.getPassword());
        IssuedAccessToken accessToken = loginUserUseCase.login(command);

        return new IssuedAccessTokenResource(accessToken.getValue());
    }

    @AllArgsConstructor
    @Getter
    private static class LoginUserRequest {
        @JsonProperty("email_address")
        private final String emailAddress;

        @JsonProperty("password")
        private final String password;
    }

    @AllArgsConstructor
    @Getter
    private static class IssuedAccessTokenResource {
        @JsonProperty("access_token")
        private final String value;
    }
}
