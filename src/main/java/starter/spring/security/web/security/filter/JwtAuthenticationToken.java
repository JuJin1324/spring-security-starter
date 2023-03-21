package starter.spring.security.web.security.filter;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import starter.spring.security.domain.token.auth.entity.TokenType;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/08
 */

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String    jsonWebToken;
    private final TokenType tokenType;
    private       UUID      uuid;

    public JwtAuthenticationToken(String jsonWebToken, TokenType tokenType) {
        super(null);
        this.jsonWebToken = jsonWebToken;
        this.tokenType = tokenType;
    }

    @Override
    public Object getCredentials() {
        return this.tokenType;
    }

    @Override
    public Object getPrincipal() {
        return this.jsonWebToken;
    }

    public void passAuthentication(UUID uuid) {
        super.setAuthenticated(true);
        this.uuid = uuid;
    }
}
