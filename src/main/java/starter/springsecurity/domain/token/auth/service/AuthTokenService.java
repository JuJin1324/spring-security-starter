package starter.springsecurity.domain.token.auth.service;

import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.token.auth.exception.InvalidRefreshTokenException;
import starter.springsecurity.domain.token.auth.model.TokenType;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */
public interface AuthTokenService {
    /**
     * 인증 토큰(access token + refresh token) 조회
     */
    AuthTokenReadDto createAuthToken(UUID userId);

    /**
     * 인증 토큰(access token + refresh token) 업데이트
     */
    AuthTokenReadDto getRefreshedAuthToken(UUID userId);

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
