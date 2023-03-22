package starter.spring.security.domain.token.jwt;


import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;


import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2022/05/13
 * Copyright (C) 2022, Centum Factorial all rights reserved.
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonWebTokenUtils {
    private static final SignatureAlgorithm ENCRYPTION_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String             TOKEN_TYPE           = "JWT";

    /**
     * 토큰 생성
     */
    public static String createToken(String secretKey,
                                     String subject,
                                     Map<String, Object> claims,
                                     Integer expirationMinute) {
        if (ObjectUtils.isEmpty(secretKey)) {
            throw new InvalidParameterException("secretKey");
        }
        if (ObjectUtils.isEmpty(subject)) {
            throw new InvalidParameterException("subject");
        }
        if (ObjectUtils.isEmpty(claims)) {
            throw new InvalidParameterException("claims");
        }

        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", ENCRYPTION_ALGORITHM.getValue());
        headers.put("typ", TOKEN_TYPE);

        LocalDateTime nowUTC = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime expirationTimeUTC = nowUTC.plusMinutes(expirationMinute);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeader(headers)
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(Timestamp.from(nowUTC.toInstant(ZoneOffset.UTC)))
                .setExpiration(Timestamp.valueOf(expirationTimeUTC))
                .signWith(ENCRYPTION_ALGORITHM, secretKey.getBytes(StandardCharsets.UTF_8));

        return jwtBuilder.compact();
    }

    /**
     * 클레임 조회
     */
    public static Map<String, Object> getClaims(String secretKey, String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        if (ObjectUtils.isEmpty(secretKey)) {
            throw new InvalidParameterException("secretKey");
        }
        if (ObjectUtils.isEmpty(token)) {
            throw new InvalidParameterException("token");
        }

        return Jwts.parser()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)) // Set Key
                .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                .getBody();
    }
}
