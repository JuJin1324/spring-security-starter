package starter.springsecurity.domain.token.auth;

import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface AuthTokenService {
    /**
     * 인증 토큰(access token + refresh token) 조회
     */
    AuthTokenReadDto getAuthToken(Long authId);

    /**
     * 인증 토큰(access token + refresh token) 업데이트
     */
    AuthTokenReadDto updateAuthToken(String refreshToken);

    /**
     * 토큰 검증 - access token
     */
    void validateAccessToken(String accessToken);

    /**
     * 매개변수 accessToken 의 userId 와 매개변수 userId 가 일치하는지 확인
     */
    boolean isUserIdMatchedWithToken(String accessToken, Long userId);

    /**
     * userId 가져오기 - access token
     */
    Long getUserIdByAccessToken(String accessToken);

    /**
     * 리프레쉬 토큰 만료
     */
    void expireRefreshToken(Long userId) throws RefreshTokenIsAlreadyExpiredException;
}
