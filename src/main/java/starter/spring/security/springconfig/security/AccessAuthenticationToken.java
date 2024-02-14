package starter.spring.security.springconfig.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.Getter;
import starter.spring.security.accesstoken.application.domain.AccessToken;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 12/5/23
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */

@Getter
public class AccessAuthenticationToken extends AbstractAuthenticationToken {
	private static final String TOKEN_PREFIX = "Bearer ";
	private final String value;
	private final AccessToken accessToken;

	protected AccessAuthenticationToken(String value, AccessToken accessToken, boolean authenticated) {
		super(null);
		this.value = value;
		this.accessToken = accessToken;
		super.setAuthenticated(authenticated);
	}

	public static AccessAuthenticationToken of(String value) {
		validate(value);

		return new AccessAuthenticationToken(value, null, false);
	}

	public static AccessAuthenticationToken authenticated(AccessToken accessToken) {
		return new AccessAuthenticationToken(null, accessToken, true);
	}

	@Override
	public Object getCredentials() {
		return getValue();
	}

	@Override
	public Object getPrincipal() {
		return getValue();
	}

	private static void validate(String value) {
		if (isEmptyString(value) || !value.startsWith(TOKEN_PREFIX)) {
			throw new IllegalArgumentException("Bearer token needs to include a word. \"Bearer \"");
		}
	}

	private static boolean isEmptyString(String value) {
		return value == null || value.isBlank();
	}
}
