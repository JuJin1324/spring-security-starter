package starter.springsecurity.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.user.dto.UserCreateDto;
import starter.springsecurity.domain.user.dto.UserReadDto;
import starter.springsecurity.domain.user.service.UserService;

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
    public CreateUserResponse createUser(@RequestBody @Valid UserCreateDto createDto) {
        /* TODO: security context 에서 가져오기 */
        PhoneNumber phoneNumber = new PhoneNumber("82", "01012341234");
        UUID userId = userService.createUser(phoneNumber, createDto);

        return new CreateUserResponse(userId);
    }

    @GetMapping("/{userId}")
    public UserReadDto getSingleUser(@PathVariable UUID userId) {
        return userService.getSingleUser(userId);
    }

    @AllArgsConstructor
    @Getter
    static class CreateUserResponse {
        private UUID userId;
    }
}
