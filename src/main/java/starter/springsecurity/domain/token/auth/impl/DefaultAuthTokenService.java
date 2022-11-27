package starter.springsecurity.domain.token.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.token.JwtTokenProvider;
import starter.springsecurity.domain.token.auth.AuthTokenService;
import starter.springsecurity.domain.token.auth.InvalidAccessTokenException;
import starter.springsecurity.domain.token.auth.InvalidRefreshTokenException;

import javax.annotation.PostConstruct;
import java.util.HashMap;
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
    private static final long   TOKEN_VALID_MINUTES   = 60L;
    private static final String PAYLOAD_USER_ID_KEY   = "userId";

    @Value("${token.auth.secretkey}")
    private String secretKey;

    private JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    private void postConstruct() {
        this.jwtTokenProvider = new JwtTokenProvider(secretKey);
    }

    @Override
    public AuthTokenReadDto createAuthToken(UUID userId) {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put(PAYLOAD_USER_ID_KEY, userId);

        String accessToken = jwtTokenProvider.createToken(
                ACCESS_TOKEN_SUBJECT, payload, TOKEN_VALID_MINUTES);
        String refreshToken = jwtTokenProvider.createToken(
                REFRESH_TOKEN_SUBJECT, payload, TOKEN_VALID_MINUTES);

        return new AuthTokenReadDto(accessToken, refreshToken);
    }

    @Override
    public AuthTokenReadDto updateAuthToken(String refreshToken) {
        return null;
    }




    @Override
    public boolean isUserIdMatchedWithToken(String accessToken, UUID userId) {
        return false;
    }

    @Override
    public UUID getUserId(String accessToken) {
        return null;
    }

    @Override
    public void expireRefreshToken(UUID userId) throws InvalidRefreshTokenException {

    }

    private void validateAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidAccessTokenException();
        }
    }
}
