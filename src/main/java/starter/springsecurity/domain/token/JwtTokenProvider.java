package starter.springsecurity.domain.token;

import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

public class JwtTokenProvider {
    private final String secretKeyBase64;

    public JwtTokenProvider(String secretKey) {
        this.secretKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 토큰 생성
     */
    public String createToken(String subject, Map<String, Object> payload, long tokensValidMinutes) {
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
    public Map<String, Object> getPayload(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
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
