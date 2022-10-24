package starter.springsecurity.web.provider;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenProvider {
    /* 토큰을 암호화할 secretKey: 값 세팅만 하고 실제 메서드에서 사용은 secretKeyBase64 로만 한다. */
    private static final String secretKey          = "springSecurityStarterSecretKey";
    private static final String secretKeyBase64    = Base64.getEncoder().encodeToString(secretKey.getBytes());
    /* 토큰 유효시간 30분 */
    private static final long   tokensValidMinutes = 30;

    /**
     * 토큰 생성
     */
    public static String createToken(String subject, Map<String, Object> payload) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(tokensValidMinutes);
        return Jwts.builder()
                .setHeader(headers)
                .setSubject(subject)
                .setClaims(payload)
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKeyBase64)
                .compact();
    }

    /**
     * 페이로드 조회
     */
    public static Map<String, Object> getPayload(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser()
                .setSigningKey(secretKeyBase64) // Set Key
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                .getBody();
    }

    /**
     * 토큰 검증
     */
    public static boolean validateToken(String token) {
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
