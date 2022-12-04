package starter.springsecurity.domain.token.registration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import starter.springsecurity.domain.token.JwtTokenProvider;
import starter.springsecurity.domain.token.registration.exception.InvalidRegistrationTokenException;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;

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
public class DefaultRegistrationTokenService implements RegistrationTokenService {
    private static final String REGISTRATION_TOKEN_SUBJECT = "RegistrationToken";
    private static final long   TOKEN_VALID_MINUTES = 3L;
    private static final String CLAIMS_AUTH_ID_KEY  = "authId";

    @Value("${token.registration.secretkey}")
    private String secretKey;

    private JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    private void postConstruct() {
        this.jwtTokenProvider = new JwtTokenProvider(secretKey);
    }

    @Override
    public String createRegistrationToken(UUID authId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_AUTH_ID_KEY, authId.toString());

        return jwtTokenProvider.createToken(REGISTRATION_TOKEN_SUBJECT, claims, TOKEN_VALID_MINUTES);
    }

    @Override
    public UUID getAuthId(String registrationToken) {
        validateRegistrationToken(registrationToken);
        
        Map<String, Object> claims = jwtTokenProvider.getClaims(registrationToken);
        return UUID.fromString((String) claims.get(CLAIMS_AUTH_ID_KEY));
    }

    private void validateRegistrationToken(String registrationToken) {
        if (!jwtTokenProvider.validateToken(registrationToken)) {
            throw new InvalidRegistrationTokenException();
        }
    }
}
