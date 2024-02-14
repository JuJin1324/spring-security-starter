package starter.spring.security.accesstoken.adapter.in.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.in.ParseAccessTokenUseCase;
import starter.spring.security.accesstoken.application.port.out.ExpiredAccessTokenException;
import starter.spring.security.accesstoken.application.port.out.InvalidAccessTokenException;
import starter.spring.security.springconfig.security.AccessAuthenticationToken;

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
		var accessAuthenticationToken = (AccessAuthenticationToken)authentication;
		var tokenValue = (String)accessAuthenticationToken.getCredentials();
		var accessToken = parse(tokenValue);

		return AccessAuthenticationToken.authenticated(accessToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AccessAuthenticationToken.class.isAssignableFrom(authentication);
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
