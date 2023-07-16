package starter.spring.security.accesstoken.adapter.out.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

public class IssuedJwt {
    private final String secretKey;
    private final Claims claims;
    private final SignatureAlgorithm algorithm;
    private final String subject;
    private final Map<String, Object> headers;
    private final LocalDateTime expirationTime;

    public IssuedJwt(SignatureAlgorithm algorithm, String secretKey,
                     Claims claims, String subject,
                     int validMinutes) {
        this.algorithm = algorithm;
        this.secretKey = secretKey;
        this.claims = claims;
        this.subject = subject;

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", algorithm.getValue());
        headers.put("typ", "JWT");
        this.headers = headers;

        this.expirationTime = LocalDateTime.now().plusMinutes(validMinutes);
    }

    @Override
    public String toString() {
        return Jwts.builder()
                .setHeader(headers)
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                .setExpiration(Timestamp.valueOf(expirationTime))
                .signWith(algorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Claims getClaims() {
        return claims;
    }
}
