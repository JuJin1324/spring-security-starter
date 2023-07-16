package starter.spring.security.accesstoken.adapter.out.jwt;

import io.jsonwebtoken.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Getter
@EqualsAndHashCode
public class ParsedJwt {
    public static final String CLAIM_KEY_USER_ID = "userId";

    private final String secretKey;
    private final Claims claims;

    public ParsedJwt(String secretKey, Claims claims) {
        this.secretKey = secretKey;
        this.claims = claims;
    }

    public static ParsedJwt parse(String secretKey, String value)
            throws ExpiredJsonWebTokenException, InvalidJsonWebTokenException {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(value)
                    .getBody();

            return new ParsedJwt(secretKey, claims);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJsonWebTokenException(e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new InvalidJsonWebTokenException(e.getMessage());
        }
    }
}
