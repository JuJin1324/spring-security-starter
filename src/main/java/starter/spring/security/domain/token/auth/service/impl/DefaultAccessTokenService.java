package starter.spring.security.domain.token.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.token.JsonWebTokenProvider;
import starter.spring.security.domain.token.auth.entity.RefreshToken;
import starter.spring.security.domain.token.auth.exception.InvalidAccessTokenException;
import starter.spring.security.domain.token.auth.exception.RefreshTokenNotFoundException;
import starter.spring.security.domain.token.auth.repository.RefreshTokenRepository;
import starter.spring.security.domain.token.auth.exception.InvalidRefreshTokenException;
import starter.spring.security.domain.token.auth.service.AccessTokenService;

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
public class DefaultAccessTokenService implements AccessTokenService {
    private static final String ACCESS_TOKEN_SUBJECT  = "AccessToken";
    private static final long   TOKEN_VALID_MINUTES   = 60L;
    private static final String CLAIMS_USER_ID_KEY    = "userId";

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${token.auth.access.secretkey}")
    private String accessTokenSecretKey;

    private JsonWebTokenProvider accessTokenProvider;

    @PostConstruct
    private void postConstruct() {
        this.accessTokenProvider = new JsonWebTokenProvider(accessTokenSecretKey);
    }

    @Override
    @Transactional
    public AccessToken createAccessToken(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseGet(() -> refreshTokenRepository.save(new RefreshToken(userId)));

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_USER_ID_KEY, refreshToken.getUserId());
        AccessToken authToken = generateAuthToken(claims);

        refreshToken.updateToken(authToken.getRefreshToken());

        return authToken;
    }

    @Override
    @Transactional
    public AccessToken updateAccessToken(UUID refreshToken) {
        RefreshToken foundRefreshToken = refreshTokenRepository.findByUserId(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (foundRefreshToken.isExpired()) {
            throw new InvalidRefreshTokenException("Refresh token has expired.");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_USER_ID_KEY, foundRefreshToken.getUserId());
        AccessToken authToken = generateAuthToken(claims);

        foundRefreshToken.updateToken(authToken.getRefreshToken());

        return authToken;
    }

    @Override
    public void verifyAccessToken(String accessToken) {
        if (!accessTokenProvider.validateToken(accessToken)) {
            throw new InvalidAccessTokenException("Token is invalid.");
        }
    }

    @Override
    public boolean isUserIdMatchedWithToken(String accessToken, UUID userId) {
        verifyAccessToken(accessToken);

        Map<String, Object> claims = accessTokenProvider.getClaims(accessToken);
        String foundUserId = (String) claims.get(CLAIMS_USER_ID_KEY);

        return userId.equals(UUID.fromString(foundUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getUserId(String accessToken) {
        verifyAccessToken(accessToken);
        Map<String, Object> claims = accessTokenProvider.getClaims(accessToken);
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

    private AccessToken generateAuthToken(Map<String, Object> claims) {
        String accessToken = accessTokenProvider.createToken(
                ACCESS_TOKEN_SUBJECT, claims, TOKEN_VALID_MINUTES);
        // TODO
        String refreshToken = UUID.randomUUID().toString();

        return new AccessToken(accessToken, refreshToken);
    }
}
