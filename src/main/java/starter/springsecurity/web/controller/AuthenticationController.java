package starter.springsecurity.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.token.registration.RegistrationTokenService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    private final RegistrationTokenService registrationTokenService;

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
        UUID authId = null; /* TODO: SpringSecurity 에서 구현해서 securityContext 받아오기 */
        AuthTokenReadDto readDto = authenticationService.createAuthToken(authId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(readDto);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    static class VerifyPhoneAuthRequest {
        @NotBlank
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
