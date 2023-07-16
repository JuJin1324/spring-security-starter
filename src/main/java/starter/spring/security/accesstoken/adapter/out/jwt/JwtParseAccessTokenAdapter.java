package starter.spring.security.accesstoken.adapter.out.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.out.ParseAccessTokenPort;
import starter.spring.security.user.application.domain.UserId;

import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Component
@RequiredArgsConstructor
public class JwtParseAccessTokenAdapter implements ParseAccessTokenPort {
    @Value("${token.access.jwt.secretkey}")
    private String secretKey;

    @Override
    public AccessToken parse(String token) {
        Map<String, Object> claims = ParsedJwt.parse(secretKey, token).getClaims();
        String userId = (String) claims.get(ParsedJwt.CLAIM_KEY_USER_ID);

        return new AccessToken(token, new UserId(userId));
    }
}
