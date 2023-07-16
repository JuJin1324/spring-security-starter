package starter.spring.security.accesstoken.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import starter.spring.security.user.application.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
class IssuedJwtTest {
    private static final String SECRET_KEY = "secretKey-test";

    @Test
    @DisplayName("신규 발급이 성공적이면 JWT 를 반환")
    void whenIssueNewSuccessfullyThenReturnJWT() {
        /* given */
        UserId userId = givenUserId();
        DefaultClaims expected = givenClaims(userId);

        /* when */

        IssuedJwt jwt = new IssuedJwt(
                SignatureAlgorithm.HS256,
                SECRET_KEY,
                expected,
                "AccessToken",
                10);

        /* then */
        assertClaims(expected, jwt.getClaims());
    }

    private void assertClaims(Claims expected, Claims actual) {
        assertEquals(expected.getIssuedAt(), actual.getIssuedAt());
        assertEquals(expected.getSubject(), actual.getSubject());
        assertEquals(expected.get(ParsedJwt.CLAIM_KEY_USER_ID), actual.get(ParsedJwt.CLAIM_KEY_USER_ID));
    }

    private DefaultClaims givenClaims(UserId userId) {
        DefaultClaims claims = new DefaultClaims();
        claims.put(ParsedJwt.CLAIM_KEY_USER_ID, userId.getValue().toString());

        return claims;
    }

    private UserId givenUserId() {
        return new UserId(UUID.randomUUID());
    }
}
