package starter.spring.security.domain.authentication.service;

import starter.spring.security.domain.entity.vo.PhoneNumber;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */
public interface AuthenticationService {
    /**
     * 폰 인증 생성
     */
    void createPhoneAuthentication(PhoneNumber phoneNumber);

    /**
     * 폰 인증 검증
     */
    void verifyPhoneAuthentication(PhoneNumber phoneNumber, String verificationCode);
}
