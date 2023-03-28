package starter.spring.security.domain.user.controller;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import starter.spring.security.domain.user.dto.UserCreateDto;
import starter.spring.security.domain.user.dto.UserReadDto;
import starter.spring.security.domain.user.service.UserService;
import starter.spring.security.global.resolver.argument.Authenticated;

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
    private final UserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createUser(@Authenticated UUID authenticationToken,
                                         @RequestBody @Valid UserCreateDto createDto) {
        UUID userId = userService.createUser(authenticationToken, createDto);

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
