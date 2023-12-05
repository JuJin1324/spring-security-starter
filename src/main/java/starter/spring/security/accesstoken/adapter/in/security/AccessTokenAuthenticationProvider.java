package starter.spring.security.accesstoken.adapter.in.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.in.ParseAccessTokenUseCase;
import starter.spring.security.accesstoken.application.port.out.ExpiredAccessTokenException;
import starter.spring.security.accesstoken.application.port.out.InvalidAccessTokenException;
import starter.spring.security.springconfig.security.BearerAuthenticationToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    private final ParseAccessTokenUseCase parseAccessTokenUseCase;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var bearerAuthenticationToken = (BearerAuthenticationToken) authentication;
        var bearerToken = (String) bearerAuthenticationToken.getCredentials();
        var accessToken = parse(bearerToken);

        return new AccessAuthenticationToken(accessToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private AccessToken parse(String bearerToken) {
        try {
            return parseAccessTokenUseCase.parse(bearerToken);
        } catch (ExpiredAccessTokenException e) {
            throw new CredentialsExpiredException(e.getMessage());
        } catch (InvalidAccessTokenException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }
}
