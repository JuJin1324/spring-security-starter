package starter.spring.security.web.security.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Getter
public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {
    private final String    accessToken;

    private       UUID      userId;

    protected AccessTokenAuthenticationToken(String accessToken) {
        super(null);
        this.accessToken = accessToken;
    }

    public static AccessTokenAuthenticationToken of(String accessToken) {
        return new AccessTokenAuthenticationToken(accessToken);
    }

    @Override
    public Object getCredentials() {
        return this.userId;
    }

    @Override
    public Object getPrincipal() {
        return this.accessToken;
    }

    public void passAuthentication() {
        super.setAuthenticated(true);
    }

    public void updateUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean hasNoPrincipal() {
        return ObjectUtils.isEmpty(this.getPrincipal());
    }
}
