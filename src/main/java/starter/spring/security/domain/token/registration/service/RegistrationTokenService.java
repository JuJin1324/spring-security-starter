package starter.spring.security.domain.token.registration.service;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface RegistrationTokenService {
    /**
     * 등록 토큰 생성
     */
    String createRegistrationToken(UUID authId);

    /**
     * authId 가져오기
     */
    UUID getAuthId(String registrationToken);
}
