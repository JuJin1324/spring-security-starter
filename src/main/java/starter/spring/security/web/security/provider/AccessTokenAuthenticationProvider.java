package starter.spring.security.web.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import starter.spring.security.domain.token.exception.ExpiredAccessTokenException;
import starter.spring.security.domain.token.exception.InvalidAccessTokenException;
import starter.spring.security.domain.token.service.AccessTokenService;
import starter.spring.security.web.security.filter.AccessTokenAuthenticationToken;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    private final AccessTokenService accessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AccessTokenAuthenticationToken accessTokenAuthenticationToken = (AccessTokenAuthenticationToken) authentication;
        if (!accessTokenAuthenticationToken.hasAccessToken()) {
            throw new BadCredentialsException("Has no access token.");
        }

        String accessToken = (String) accessTokenAuthenticationToken.getPrincipal();
        try {
            accessTokenService.verifyAccessToken(accessToken);
        } catch (InvalidAccessTokenException invalidEx) {
            throw new BadCredentialsException(invalidEx.getMessage());
        } catch (ExpiredAccessTokenException expiredEx) {
            throw new CredentialsExpiredException(expiredEx.getMessage());
        }

        accessTokenAuthenticationToken.passAuthentication();

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
