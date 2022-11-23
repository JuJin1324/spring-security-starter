package starter.springsecurity.domain.authentication.service;

import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.dto.CodeAuthVerificationResult;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.entity.vo.PhoneNumber;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */
public interface AuthenticationService {
    void createPhoneAuth(PhoneAuthCreateDto createDto);

    CodeAuthVerificationResult verifyPhoneAuth(PhoneNumber phoneNumber, String verificationCode);

    AuthTokenReadDto getAuthToken(UUID authId);
}
