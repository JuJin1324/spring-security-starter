package starter.spring.security.accesstoken.adapter.out.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.out.IssueAccessTokenPort;
import starter.spring.security.user.application.domain.UserId;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Component
@RequiredArgsConstructor
public class JwtIssueAccessTokenAdapter implements IssueAccessTokenPort {

    @Value("${token.access.jwt.secretkey}")
    private String secretKey;

    @Override
    public AccessToken issueNewOne(UserId id) {
        DefaultClaims claims = new DefaultClaims();
        claims.put(ParsedJwt.CLAIM_KEY_USER_ID, id.getValue().toString());

        String jwt = new IssuedJwt(
                SignatureAlgorithm.HS256,
                secretKey,
                claims,
                "AccessToken",
                60
        ).toString();

        return new AccessToken(jwt, id);
    }
}

