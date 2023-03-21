package starter.spring.security.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.token.auth.service.AccessTokenService;
import starter.spring.security.domain.user.service.UserService;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/tokens")
public class TokenController {
    private final UserService        userService;
    private final AccessTokenService accessTokenService;

    /**
     * 엑세스 토큰 조회
     */
    @GetMapping("/access")
    public ResponseEntity<AccessToken> getAccessToken(@RequestHeader(AUTHORIZATION) UUID authenticationToken) {
        UUID userId = userService.getUserId(authenticationToken);
        AccessToken accessToken = accessTokenService.createAccessToken(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(accessToken);
    }

    /**
     * 엑세스 토큰 업데이트
     */
    @PutMapping("/access")
    public ResponseEntity<AccessToken> updateAccessToken(@RequestHeader(AUTHORIZATION) UUID refreshToken) {
        AccessToken accessToken = accessTokenService.updateAccessToken(refreshToken);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(accessToken);
    }
}
