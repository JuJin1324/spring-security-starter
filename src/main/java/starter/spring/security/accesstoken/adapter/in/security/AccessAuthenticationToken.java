package starter.spring.security.accesstoken.adapter.in.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.ObjectUtils;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.springconfig.security.BearerToken;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Getter
public class AccessAuthenticationToken extends AbstractAuthenticationToken {
    private final AccessToken accessToken;

    protected AccessAuthenticationToken(AccessToken accessToken) {
        super(null);
        super.setAuthenticated(true);
        this.accessToken = accessToken;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return accessToken;
    }
}
