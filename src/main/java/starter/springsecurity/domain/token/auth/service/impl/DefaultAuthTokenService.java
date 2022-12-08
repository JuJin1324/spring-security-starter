package starter.springsecurity.domain.token.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.token.JsonWebTokenProvider;
import starter.springsecurity.domain.token.auth.exception.InvalidAccessTokenException;
import starter.springsecurity.domain.token.auth.exception.InvalidRefreshTokenException;
import starter.springsecurity.domain.token.auth.exception.RefreshTokenNotFoundException;
import starter.springsecurity.domain.token.auth.model.RefreshToken;
import starter.springsecurity.domain.token.auth.model.TokenType;
import starter.springsecurity.domain.token.auth.repository.RefreshTokenRepository;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Service
@RequiredArgsConstructor
public class DefaultAuthTokenService implements AuthTokenService {
    private static final String ACCESS_TOKEN_SUBJECT  = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final long   TOKEN_VALID_MINUTES = 60L;
    private static final String CLAIMS_USER_ID_KEY  = "userId";

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${token.auth.secretkey}")
    private String secretKey;

    private JsonWebTokenProvider jsonWebTokenProvider;

    @PostConstruct
    private void postConstruct() {
        this.jsonWebTokenProvider = new JsonWebTokenProvider(secretKey);
    }

    @Override
    @Transactional
    public AuthTokenReadDto createAuthToken(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseGet(() -> refreshTokenRepository.save(new RefreshToken(userId)));

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_USER_ID_KEY, refreshToken.getUserId());
        AuthTokenReadDto authToken = generateAuthToken(claims);

        refreshToken.updateToken(authToken.getRefreshToken());

        return authToken;
    }

    @Override
    @Transactional
    public AuthTokenReadDto getRefreshedAuthToken(UUID userId) {
        RefreshToken foundRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (foundRefreshToken.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token has expired.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_USER_ID_KEY, foundRefreshToken.getUserId());
        AuthTokenReadDto authToken = generateAuthToken(claims);

        foundRefreshToken.updateToken(authToken.getRefreshToken());

        return authToken;
    }

    @Override
    public boolean isUserIdMatchedWithToken(String accessToken, UUID userId) {
        validateAccessToken(accessToken);

        Map<String, Object> claims = jsonWebTokenProvider.getClaims(accessToken);
        String foundUserId = (String) claims.get(CLAIMS_USER_ID_KEY);

        return userId.equals(UUID.fromString(foundUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getUserId(String token, TokenType tokenType) {
        if (tokenType.equals(TokenType.ACCESS)) {
            validateAccessToken(token);
        } else {
            validateRefreshToken(token);
        }

        Map<String, Object> claims = jsonWebTokenProvider.getClaims(token);
        String foundUserId = (String) claims.get(CLAIMS_USER_ID_KEY);

        return UUID.fromString(foundUserId);
    }

    @Override
    @Transactional
    public void expireRefreshToken(UUID userId) throws InvalidRefreshTokenException {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);
        refreshToken.expire();
    }

    private AuthTokenReadDto generateAuthToken(Map<String, Object> claims) {
        String accessToken = jsonWebTokenProvider.createToken(
                ACCESS_TOKEN_SUBJECT, claims, TOKEN_VALID_MINUTES);
        String refreshToken = jsonWebTokenProvider.createToken(
                REFRESH_TOKEN_SUBJECT, claims, TOKEN_VALID_MINUTES);

        return new AuthTokenReadDto(accessToken, refreshToken);
    }

    private void validateAccessToken(String accessToken) {
        if (!jsonWebTokenProvider.validateToken(accessToken)) {
            throw new InvalidAccessTokenException("Token is invalid.");
        }
        if (!jsonWebTokenProvider.getSubject(accessToken).equals(ACCESS_TOKEN_SUBJECT)) {
            throw new InvalidAccessTokenException("Subject is not right.");
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jsonWebTokenProvider.validateToken(refreshToken)) {
            throw new InvalidRefreshTokenException("Token is invalid.");
        }

        RefreshToken foundRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (foundRefreshToken.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token has expired.");
        }
    }
}
