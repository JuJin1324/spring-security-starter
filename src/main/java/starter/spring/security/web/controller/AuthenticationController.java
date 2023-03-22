package starter.spring.security.web.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starter.spring.security.domain.authentication.dto.PhoneAuthenticationCreateDto;
import starter.spring.security.domain.authentication.service.AuthenticationService;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentications")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * 전화번호 인증 생성
     */
    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPhoneAuth(@RequestBody @Valid PhoneAuthenticationCreateDto createDto) {
        authenticationService.createPhoneAuthentication(createDto.getPhoneNumber());
    }

    /**
     * 전화번호 인증 검증
     */
    @PutMapping("/phone/verify")
    public ResponseEntity<VerifyPhoneAuthResponse> verifyPhoneAuth(@RequestBody @Valid VerifyPhoneAuthRequest request) {
        PhoneNumber phoneNumber = request.getPhoneNumber();
        String verificationCode = request.getVerificationCode();

        authenticationService.verifyPhoneAuthentication(phoneNumber, verificationCode);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(new VerifyPhoneAuthResponse(null));
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Setter
    @Getter
    public static class VerifyPhoneAuthRequest {
        @NotBlank
        private String countryCode;

        @NotBlank
        private String phoneNo;
        @NotBlank
        private String verificationCode;

        @JsonIgnore
        public PhoneNumber getPhoneNumber() {
            return new PhoneNumber(this.countryCode, this.phoneNo);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class VerifyPhoneAuthResponse {
        private String authenticationToken;
    }
}
