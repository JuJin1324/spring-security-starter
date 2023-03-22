package starter.spring.security.domain.token.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.token.entity.RefreshToken;
import starter.spring.security.domain.token.exception.*;
import starter.spring.security.domain.token.jwt.JsonWebTokenUtils;
import starter.spring.security.domain.token.repository.RefreshTokenRepository;
import starter.spring.security.domain.token.service.AccessTokenService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Service
@RequiredArgsConstructor
public class JwtAccessTokenService implements AccessTokenService {
    private static final String ACCESS_TOKEN_SUBJECT  = "AccessToken";
    private static final int   TOKEN_VALID_MINUTES   = 60;
    private static final String CLAIMS_USER_ID_KEY    = "userId";

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${token.access.secretkey}")
    private String accessTokenSecretKey;

    @Override
    @Transactional
    public AccessToken createAccessToken(UUID authenticationToken) {
        RefreshToken refreshToken = refreshTokenRepository.findWithUserByToken(authenticationToken)
                .orElseThrow(RefreshTokenNotFoundException::new);

        String accessToken = generateAccessToken(refreshToken.getUserId());
        refreshToken.update();

        return new AccessToken(accessToken, refreshToken.getToken().toString());
    }

    @Override
    @Transactional
    public AccessToken updateAccessToken(UUID refreshToken) {
        RefreshToken foundRefreshToken = refreshTokenRepository.findWithUserByToken(refreshToken)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (foundRefreshToken.hasExpired()) {
            throw new ExpiredRefreshTokenException();
        }

        String accessToken = generateAccessToken(foundRefreshToken.getUserId());
        foundRefreshToken.update();

        return new AccessToken(accessToken, foundRefreshToken.getToken().toString());
    }

    @Override
    public void verifyAccessToken(String accessToken) throws InvalidAccessTokenException, ExpiredAccessTokenException {
        try {
            JsonWebTokenUtils.getClaims(accessTokenSecretKey, accessToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredAccessTokenException();
        } catch (Exception e) {
            throw new InvalidAccessTokenException();
        }
    }

    @Override
    public boolean isUserIdMatchedWithToken(String accessToken, UUID userId) {
        Map<String, Object> claims = JsonWebTokenUtils.getClaims(accessTokenSecretKey, accessToken);
        String foundUserId = (String) claims.get(CLAIMS_USER_ID_KEY);

        return userId.equals(UUID.fromString(foundUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getUserId(String accessToken) {
        Map<String, Object> claims = JsonWebTokenUtils.getClaims(accessTokenSecretKey, accessToken);
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

    private String generateAccessToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_USER_ID_KEY, userId);

        return JsonWebTokenUtils.createToken(accessTokenSecretKey,
                ACCESS_TOKEN_SUBJECT, claims, TOKEN_VALID_MINUTES);
    }
}
