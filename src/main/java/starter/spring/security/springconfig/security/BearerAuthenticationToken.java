package starter.spring.security.springconfig.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

public class BearerAuthenticationToken extends AbstractAuthenticationToken {
    private final BearerToken bearerToken;

    protected BearerAuthenticationToken(BearerToken bearerToken) {
        super(null);
        this.bearerToken = bearerToken;
    }

    public static BearerAuthenticationToken of(BearerToken value) {
        return new BearerAuthenticationToken(value);
    }

    public static BearerAuthenticationToken emptyToken() {
        return new BearerAuthenticationToken(null);
    }

    private boolean isEmptyToken() {
        return ObjectUtils.isEmpty(this.bearerToken);
    }

    private BearerToken getBearerToken() {
        if (this.isEmptyToken()) {
            throw new BadCredentialsException("Has no access token.");
        }
        return bearerToken;
    }

    @Override
    public Object getCredentials() {
        return getBearerToken();
    }

    @Override
    public Object getPrincipal() {
        return getBearerToken();
    }
}
