package starter.spring.security.user.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import starter.spring.security.user.application.domain.Email;
import starter.spring.security.user.application.domain.UserId;
import starter.spring.security.user.application.port.in.SignUpUserCommand;
import starter.spring.security.user.application.port.in.SignUpUserUseCase;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RestController
@RequiredArgsConstructor
public class SignUpUserController {
    private final SignUpUserUseCase signUpUserUseCase;

    @PostMapping("/users")
    public SignUpResultResource signUpUser(@RequestBody SignUpUserRequest request) {
        SignUpUserCommand command = new SignUpUserCommand(request.getFullName(),
                new Email(request.getEmailAddress()), request.getPassword());
        UserId userId = signUpUserUseCase.singUp(command);

        return new SignUpResultResource(userId.getValue().toString());
    }

    @AllArgsConstructor
    @Getter
    private static class SignUpUserRequest {
        @JsonProperty("full_name")
        private final String fullName;

        @JsonProperty("email_address")
        private final String emailAddress;

        @JsonProperty("password")
        private final String password;
    }


    @AllArgsConstructor
    @Getter
    private static class SignUpResultResource {
        @JsonProperty("user_id")
        private final String userId;
    }

}
