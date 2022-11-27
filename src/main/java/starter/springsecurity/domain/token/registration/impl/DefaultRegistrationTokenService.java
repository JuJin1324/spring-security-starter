package starter.springsecurity.domain.token.registration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import starter.springsecurity.domain.token.JwtTokenProvider;
import starter.springsecurity.domain.token.registration.InvalidRegistrationTokenException;
import starter.springsecurity.domain.token.registration.RegistrationTokenService;

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
    private static final long   TOKEN_VALID_MINUTES        = 3L;
    private static final String PAYLOAD_AUTH_ID_KEY        = "authId";

    @Value("${token.registration.secretkey}")
    private String secretKey;

    private JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    private void postConstruct() {
        this.jwtTokenProvider = new JwtTokenProvider(secretKey);
    }

    @Override
    public String createRegistrationToken(UUID authId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(PAYLOAD_AUTH_ID_KEY, authId.toString());

        return jwtTokenProvider.createToken(REGISTRATION_TOKEN_SUBJECT, payload, TOKEN_VALID_MINUTES);
    }

    @Override
    public UUID getAuthId(String registrationToken) {
        validateRegistrationToken(registrationToken);

        Map<String, Object> payload = jwtTokenProvider.getPayload(registrationToken);
        return (UUID) payload.get(PAYLOAD_AUTH_ID_KEY);
    }

    private void validateRegistrationToken(String registrationToken) {
        if (!jwtTokenProvider.validateToken(registrationToken)) {
            throw new InvalidRegistrationTokenException();
        }
    }
}
