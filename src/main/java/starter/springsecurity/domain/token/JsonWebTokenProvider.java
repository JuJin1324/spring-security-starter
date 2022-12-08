package starter.springsecurity.domain.token;

import io.jsonwebtoken.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

public class JsonWebTokenProvider {
    private final String secretKeyBase64;

    public JsonWebTokenProvider(String secretKey) {
        this.secretKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 토큰 생성
     */
    public String createToken(String subject, Map<String, Object> claims, long tokensValidMinutes) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(tokensValidMinutes);

        System.out.println("timestamp: " + Timestamp.from(now.toInstant(ZoneOffset.UTC)));

        return Jwts.builder()
                .setHeader(headers)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Timestamp.from(now.toInstant(ZoneOffset.UTC)))
                .setExpiration(Timestamp.valueOf(expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKeyBase64)
                .compact();
    }

    /**
     * Subject 조회
     */
    public String getSubject(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser()
                .setSigningKey(secretKeyBase64) // Set Key
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                .getBody()
                .getSubject();
    }

    /**
     * Claims 조회
     */
    public Map<String, Object> getClaims(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser()
                .setSigningKey(secretKeyBase64) // Set Key
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                .getBody();
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKeyBase64)
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException e) {
            return false;
        }
    }
}
