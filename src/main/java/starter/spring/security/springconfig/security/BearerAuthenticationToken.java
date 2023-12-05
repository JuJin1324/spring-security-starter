package starter.spring.security.springconfig.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 12/5/23
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */
public class BearerAuthenticationToken extends AbstractAuthenticationToken {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final String value;

    protected BearerAuthenticationToken(String value) {
        super(null);
        this.value = value;
    }

    public static BearerAuthenticationToken of(String value) {
        validate(value);

        return new BearerAuthenticationToken(value);
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

    private static String extractValue(String bearerToken) {
        return bearerToken.substring(TOKEN_PREFIX.length());
    }

    private static boolean isEmptyString(String value) {
        return value == null || value.isBlank();
    }

    private boolean isEmptyToken() {
        return ObjectUtils.isEmpty(this.value);
    }

    private String getValue() {
        if (this.isEmptyToken()) {
            throw new BadCredentialsException("Has no access token.");
        }
        return value;
    }
}
