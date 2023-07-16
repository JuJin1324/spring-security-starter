package starter.spring.security.accesstoken.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.in.ParseAccessTokenUseCase;
import starter.spring.security.accesstoken.application.port.out.ParseAccessTokenPort;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Service
@RequiredArgsConstructor
public class ParseAccessTokenService implements ParseAccessTokenUseCase {
    private final ParseAccessTokenPort parseAccessTokenPort;

    @Override
    public AccessToken parse(String token) {
        return parseAccessTokenPort.parse(token);
    }
}
