package starter.spring.security.springconfig.security;

import lombok.Getter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Getter
public class BearerToken {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final String value;

    public BearerToken(String value) {
        validate(value);
        this.value = extractValue(value);
    }

    private void validate(String value) {
        if (isEmptyString(value) || !value.startsWith(TOKEN_PREFIX)) {
            throw new InvalidBearerTokenException();
        }
    }

    private String extractValue(String bearerToken) {
        return bearerToken.substring(TOKEN_PREFIX.length());
    }

    private boolean isEmptyString(String value) {
        return value == null || value.isBlank();
    }
}
