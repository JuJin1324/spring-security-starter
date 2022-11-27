package starter.springsecurity.domain.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.model.PhoneAuth;
import starter.springsecurity.domain.authentication.repository.PhoneAuthRepository;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {
    private static final int    VALID_AUTHENTICATION_MINUTES = 3;
    private static final int    VERIFICATION_CODE_DIGITS     = 6;
    private static final Random RANDOM                       = new Random();

    private final PhoneAuthRepository phoneAuthRepository;

    @Override
    public UUID createPhoneAuth(PhoneNumber phoneNumber) {
        PhoneAuth phoneAuth = phoneAuthRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> phoneAuthRepository.save(new PhoneAuth(phoneNumber)));
        phoneAuth.updateAuth(generateVerificationCode(), VALID_AUTHENTICATION_MINUTES);

        return phoneAuth.getUuid();
    }

    @Override
    public void verifyPhoneAuth(UUID authId, String verificationCode) {
        PhoneAuth phoneAuth = phoneAuthRepository.findByUuid(authId)
                .orElseThrow();

        if (!phoneAuth.verifyExpirationTime()) {
            throw new RuntimeException("PhoneAuth has expired.");
        }
        if (!phoneAuth.verifyCode(verificationCode)) {
            throw new RuntimeException("Tried invalid verification code.");
        }
    }

    @Override
    public AuthTokenReadDto createAuthToken(UUID authId) {
        return null;
    }

    private String generateVerificationCode() {
        return String.format("%0" + DefaultAuthenticationService.VERIFICATION_CODE_DIGITS + "d",
                RANDOM.nextInt((int) Math.pow(10, DefaultAuthenticationService.VERIFICATION_CODE_DIGITS)));
    }
}
