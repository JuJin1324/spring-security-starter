package starter.spring.security.web.controller;

import lombok.*;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starter.spring.security.domain.authentication.dto.PhoneAuthCreateDto;
import starter.spring.security.domain.authentication.service.AuthenticationService;
import starter.spring.security.domain.token.registration.service.RegistrationTokenService;

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
@RequestMapping("/authentications")
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
    @PutMapping("/phone/verify")
    public ResponseEntity<VerifyPhoneAuthResponse> verifyPhoneAuth(@RequestBody @Valid VerifyPhoneAuthRequest request) {

        UUID authId = request.getAuthId();
        String verificationCode = request.getVerificationCode();

        authenticationService.verifyPhoneAuth(authId, verificationCode);
        String registrationToken = registrationTokenService.createRegistrationToken(authId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(new VerifyPhoneAuthResponse(registrationToken));
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Setter
    @Getter
    public static class VerifyPhoneAuthRequest {
        @NotNull
        private UUID   authId;
        @NotBlank
        private String verificationCode;
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class VerifyPhoneAuthResponse {
        private String registrationToken;
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class CreatePhoneAuthResponse {
        private UUID authId;
    }
}
