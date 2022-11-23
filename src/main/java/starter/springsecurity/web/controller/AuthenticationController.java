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
import starter.springsecurity.domain.authentication.dto.CodeAuthVerificationResult;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;

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
    private final AuthenticationService authenticationService;

    /**
     * 전화번호 인증 생성
     */
    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPhoneAuth(@RequestBody @Valid PhoneAuthCreateDto createDto) {
        authenticationService.createPhoneAuth(createDto);
    }

    /**
     * 전화번호 인증 검증
     */
    @PostMapping(value = "/phone", params = "verify=true")
    public ResponseEntity<CodeAuthVerificationResult> verifyPhoneAuth(
            @RequestBody @Valid VerifyPhoneAuthRequest request) {

        CodeAuthVerificationResult result = authenticationService.verifyPhoneAuth(
                request.getPhoneNumber(), request.getVerificationCode());

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(result);
    }

    /**
     * 인증 토큰 조회
     */
    @GetMapping("/token")
    public ResponseEntity<AuthTokenReadDto> getAuthToken(@RequestParam("authId") UUID authId) {
        AuthTokenReadDto readDto = authenticationService.getAuthToken(authId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(readDto);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static class VerifyPhoneAuthRequest {
        @NotBlank
        private String countryCode;
        @NotBlank
        private String phoneNo;
        @NotBlank
        @Getter
        private String verificationCode;

        public PhoneNumber getPhoneNumber() {
            return new PhoneNumber(countryCode, phoneNo);
        }
    }
}
