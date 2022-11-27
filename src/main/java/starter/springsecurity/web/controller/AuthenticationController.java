package starter.springsecurity.web.controller;

import lombok.*;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.authentication.repository.PhoneAuthRepository;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.token.auth.AuthTokenService;
import starter.springsecurity.domain.token.registration.RegistrationTokenService;
import starter.springsecurity.domain.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService    authenticationService;
    private final UserService              userService;
    private final RegistrationTokenService registrationTokenService;
    private final AuthTokenService         authTokenService;

    // TODO: 제거
    private final PhoneAuthRepository phoneAuthRepository;

    /**
     * 전화번호 인증 생성
     */
    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePhoneAuthResponse createPhoneAuth(@RequestBody @Valid PhoneAuthCreateDto createDto) {
        UUID authId = authenticationService.createPhoneAuth(createDto.getPhoneNumber());
        return new CreatePhoneAuthResponse(authId);
    }

    /**
     * 전화번호 인증 검증
     */
    @PostMapping(value = "/phone", params = "verify=true")
    public ResponseEntity<String> verifyPhoneAuth(
            @RequestBody @Valid VerifyPhoneAuthRequest request) {

        UUID authId = request.getAuthId();
        String verificationCode = request.getVerificationCode();

        authenticationService.verifyPhoneAuth(authId, verificationCode);
        String registrationToken = registrationTokenService.createRegistrationToken(authId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(registrationToken);
    }

    /**
     * 인증 토큰 조회
     */
    @GetMapping("/token")
    public ResponseEntity<AuthTokenReadDto> getAuthToken() {
        /* TODO: SpringSecurity 에서 구현해서 securityContext 받아오기 */
        UUID authId = phoneAuthRepository.findByPhoneNumber(new PhoneNumber("82", "01012341234"))
                .orElseThrow().getUuid();
        UUID userId = userService.getUserId(authId);

        AuthTokenReadDto authToken = authTokenService.createAuthToken(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(authToken);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    static class VerifyPhoneAuthRequest {
        @NotNull
        private UUID   authId;
        @NotBlank
        private String verificationCode;
    }

    @AllArgsConstructor
    @Getter
    static class CreatePhoneAuthResponse {
        private UUID authId;
    }
}
