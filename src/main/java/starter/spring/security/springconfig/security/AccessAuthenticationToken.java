package starter.spring.security.springconfig.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.ObjectUtils;
import starter.spring.security.accesstoken.application.domain.AccessToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Getter
public class AccessAuthenticationToken extends AbstractAuthenticationToken {
    private final BearerToken bearerToken;
    private AccessToken accessToken;

    protected AccessAuthenticationToken(BearerToken bearerToken) {
        super(null);
        this.bearerToken = bearerToken;
    }

    public static AccessAuthenticationToken of(BearerToken value) {
        return new AccessAuthenticationToken(value);
    }

    public static AccessAuthenticationToken emptyToken() {
        return new AccessAuthenticationToken(null);
    }

    public void passAuthentication(AccessToken accessToken) {
        super.setAuthenticated(true);
        this.accessToken = accessToken;
    }

    public boolean isEmptyToken() {
        return ObjectUtils.isEmpty(this.bearerToken);
    }

    @Override
    public Object getCredentials() {
        return bearerToken;
    }

    @Override
    public Object getPrincipal() {
        return bearerToken;
    }
}
