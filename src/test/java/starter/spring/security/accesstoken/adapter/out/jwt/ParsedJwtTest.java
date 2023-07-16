package starter.spring.security.accesstoken.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import starter.spring.security.user.application.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
class ParsedJwtTest {
    private static final String SECRET_KEY = "secretKey-test";

    @Test
    @DisplayName("파싱이 성공적이면 JWT 를 반환")
    void whenParseSuccessfullyThenReturnJWT() {
        /* given */
        UserId userId = givenUserId();
        DefaultClaims expected = givenClaims(userId);
        IssuedJwt issuedJwt = new IssuedJwt(
                SignatureAlgorithm.HS256,
                SECRET_KEY,
                expected,
                "AccessToken",
                10);

        /* when */
        ParsedJwt parsedJwt = ParsedJwt.parse(SECRET_KEY, issuedJwt.toString());

        /* then */
        assertClaims(expected, parsedJwt.getClaims());
    }

    @Test
    @DisplayName("유효하지 않은 JWT 를 파싱하면 에러 반환")
    void whenParseInvalidJwtThenReturnException() {
        assertThrows(InvalidJsonWebTokenException.class, () ->
                ParsedJwt.parse(SECRET_KEY, "InvalidJwt"));
    }

    @Test
    @DisplayName("만료된 JWT 를 파싱하면 에러 반환")
    void whenParseExpiredJwtThenReturnException() {
        /* given */
        UserId userId = givenUserId();
        DefaultClaims expected = givenClaims(userId);
        IssuedJwt issuedJwt = new IssuedJwt(
                SignatureAlgorithm.HS256,
                SECRET_KEY,
                expected,
                "AccessToken",
                -10);

        /* when */
        /* then */
        assertThrows(ExpiredJsonWebTokenException.class, () ->
                ParsedJwt.parse(SECRET_KEY, issuedJwt.toString()));
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
