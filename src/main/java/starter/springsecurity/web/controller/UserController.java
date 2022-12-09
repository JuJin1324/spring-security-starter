package starter.springsecurity.web.controller;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.user.dto.UserCreateDto;
import starter.springsecurity.domain.user.dto.UserReadDto;
import starter.springsecurity.domain.user.service.UserService;
import starter.springsecurity.web.resolver.argument.Authenticated;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService           userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createUser(@Authenticated UUID authId,
                                         @RequestBody @Valid UserCreateDto createDto) {
        PhoneNumber phoneNumber = authenticationService.getAuthenticatedPhoneNumber(authId);
        UUID userId = userService.createUser(phoneNumber, createDto);

        return new CreateUserResponse(userId);
    }

    @GetMapping("/{userId}")
    public UserReadDto getSingleUser(@PathVariable UUID userId) {
        return userService.getSingleUser(userId);
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class CreateUserResponse {
        private UUID userId;
    }
}
