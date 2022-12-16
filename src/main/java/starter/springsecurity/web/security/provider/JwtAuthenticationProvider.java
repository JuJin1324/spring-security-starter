package starter.springsecurity.web.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import starter.springsecurity.domain.token.auth.model.TokenType;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;
import starter.springsecurity.web.security.filter.JwtAuthenticationToken;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final RegistrationTokenService registrationTokenService;
    private final AuthTokenService         authTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String jsonWebToken = (String) jwtAuthenticationToken.getPrincipal();
        TokenType tokenType = (TokenType) jwtAuthenticationToken.getCredentials();

        switch (tokenType) {
            case NONE:
                jwtAuthenticationToken.setAuthenticated(false);
                break;
            case REGISTRATION:
                UUID authId = registrationTokenService.getAuthId(jsonWebToken);
                jwtAuthenticationToken.passAuthentication(authId);
                break;
            case REFRESH:
                UUID userIdFromRefresh = authTokenService.getUserId(jsonWebToken, TokenType.REFRESH);
                jwtAuthenticationToken.passAuthentication(userIdFromRefresh);
                break;
            case ACCESS:
                UUID userIdFromAccess = authTokenService.getUserId(jsonWebToken, TokenType.ACCESS);
                jwtAuthenticationToken.passAuthentication(userIdFromAccess);
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
