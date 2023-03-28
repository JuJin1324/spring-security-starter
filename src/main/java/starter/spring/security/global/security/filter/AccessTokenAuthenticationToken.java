package starter.spring.security.global.security.filter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.StringUtils;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Getter
public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {
    private final String    accessToken;

    protected AccessTokenAuthenticationToken(String accessToken) {
        super(null);
        this.accessToken = accessToken;
    }

    public static AccessTokenAuthenticationToken of(String accessToken) {
        return new AccessTokenAuthenticationToken(accessToken);
    }

    public void passAuthentication() {
        super.setAuthenticated(true);
    }

    public boolean hasAccessToken() {
        return StringUtils.hasText(this.accessToken);
    }

    @Override
    public Object getCredentials() {
        return this.accessToken;
    }

    @Override
    public Object getPrincipal() {
        return this.accessToken;
    }
}
