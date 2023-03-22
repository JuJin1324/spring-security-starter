package starter.spring.security.domain.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.spring.security.domain.authentication.entity.PhoneAuthentication;
import starter.spring.security.domain.authentication.exception.PhoneAuthNotFoundException;
import starter.spring.security.domain.authentication.repository.PhoneAuthenticationRepository;
import starter.spring.security.domain.authentication.service.AuthenticationService;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import java.time.*;
import java.util.List;
import java.util.Random;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {
    private static final int    VERIFICATION_CODE_DIGITS = 4;
    private static final Random RANDOM                   = new Random();

    private final PhoneAuthenticationRepository phoneAuthenticationRepository;

    @Override
    public void createPhoneAuthentication(PhoneNumber phoneNumber) {
        PhoneAuthentication phoneAuthentication =
                PhoneAuthentication.of(phoneNumber, generateVerificationCode());
        phoneAuthenticationRepository.save(phoneAuthentication);

        // TODO: SMS 전송
    }

    @Override
    public void verifyPhoneAuthentication(PhoneNumber phoneNumber, String verificationCode) {
        PhoneAuthentication phoneAuthentication = getTodaysLatestPhoneAuthentication(phoneNumber);
        if (phoneAuthentication.hasExpired()) {
            throw new RuntimeException("PhoneAuth has expired.");
        }
        if (!phoneAuthentication.hasMatchedVerificationCode(verificationCode)) {
            throw new RuntimeException("Tried invalid verification code.");
        }

        phoneAuthentication.passVerification();
    }

    private String generateVerificationCode() {
        return String.format("%0" + VERIFICATION_CODE_DIGITS + "d",
                RANDOM.nextInt((int) Math.pow(10, VERIFICATION_CODE_DIGITS)));
    }

    private PhoneAuthentication getTodaysLatestPhoneAuthentication(PhoneNumber phoneNumber) {
        ZoneId zoneKST = ZoneId.of("Asia/Seoul");
        ZoneId zoneUTC = ZoneId.of("UTC");
        LocalDate todayKST = LocalDate.now(zoneKST);
        LocalDateTime startTimeUTC = ZonedDateTime.of(todayKST, LocalTime.MIN, zoneKST)
                .withZoneSameInstant(zoneUTC)
                .toLocalDateTime();
        LocalDateTime endTimeUTC = ZonedDateTime.of(todayKST, LocalTime.MAX, zoneKST)
                .withZoneSameInstant(zoneUTC)
                .toLocalDateTime();

        List<PhoneAuthentication> phoneAuthentications =
                phoneAuthenticationRepository.findByPhoneNumberAndCreatedTimeUTCRanged(
                        phoneNumber, startTimeUTC, endTimeUTC);
        if (phoneAuthentications.isEmpty()) {
            throw new PhoneAuthNotFoundException();
        }

        return phoneAuthentications.stream()
                .min((o1, o2) -> {
                    long m1 = o1.getCreatedTimeUTC().atZone(zoneUTC).toInstant().toEpochMilli();
                    long m2 = o2.getCreatedTimeUTC().atZone(zoneUTC).toInstant().toEpochMilli();
                    return Math.toIntExact(m1 - m2);
                })
                .orElseThrow(PhoneAuthNotFoundException::new);
    }
}
