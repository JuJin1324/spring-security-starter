package starter.springsecurity.domain.authentication.service;

import starter.springsecurity.domain.entity.vo.PhoneNumber;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */
public interface AuthenticationService {
    /**
     * 폰 인증 생성
     *
     * @return authId 생성한 인증 ID 반환
     */
    UUID createPhoneAuth(PhoneNumber phoneNumber);

    /**
     * 폰 인증 검증
     */
    void verifyPhoneAuth(UUID authId, String verificationCode);

    /**
     * 인증된 폰 번호 조회
     */
    PhoneNumber getAuthenticatedPhoneNumber(UUID authId);
}
