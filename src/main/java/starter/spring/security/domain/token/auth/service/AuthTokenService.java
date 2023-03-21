package starter.spring.security.domain.token.auth.service;

import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.token.auth.entity.TokenType;
import starter.spring.security.domain.token.auth.exception.InvalidRefreshTokenException;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface AuthTokenService {
    /**
     * 인증 토큰(access token + refresh token) 생성
     */
    AccessToken createAccessToken(UUID authenticationToken);

    /**
     * 인증 토큰(access token + refresh token) 업데이트
     */
    AccessToken updateAccessToken(UUID refreshToken);

    /**
     * 매개변수 accessToken 의 userId 와 매개변수 userId 가 일치하는지 확인
     */
    boolean isUserIdMatchedWithToken(String accessToken, UUID userId);

    /**
     * userId 가져오기
     */
    UUID getUserId(String token, TokenType tokenType);

    /**
     * 리프레쉬 토큰 만료
     */
    void expireRefreshToken(UUID userId) throws InvalidRefreshTokenException;
}
