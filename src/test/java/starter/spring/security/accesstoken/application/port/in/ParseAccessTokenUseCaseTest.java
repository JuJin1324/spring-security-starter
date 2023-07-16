package starter.spring.security.accesstoken.application.port.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.out.ParseAccessTokenPort;
import starter.spring.security.accesstoken.application.service.ParseAccessTokenService;
import starter.spring.security.user.application.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
@ExtendWith(MockitoExtension.class)
class ParseAccessTokenUseCaseTest {
    private ParseAccessTokenUseCase parseAccessTokenUseCase;

    @Mock
    private ParseAccessTokenPort parseAccessTokenPort;
    
    @BeforeEach
    void setUp() {
        parseAccessTokenUseCase = new ParseAccessTokenService(parseAccessTokenPort);
    }

    @Test
    @DisplayName("파싱이 성공적으로 되면 엑세스 토큰 반환")
    void whenParseSuccessfullyThenReturnAccessToken() {
        /* givne */
        UserId userId = givenUserId();
        String token = "accessTokenValue";
        AccessToken expected = new AccessToken(token, userId);
        given(parseAccessTokenPort.parse(token)).willReturn(expected);
        
        /* when */
        AccessToken accessToken = parseAccessTokenUseCase.parse(token);

        /* then */
        assertEquals(expected.getUserId(), accessToken.getUserId());
        assertEquals(expected.getValue(), accessToken.getValue());
    }

    private UserId givenUserId() {
        return new UserId(UUID.randomUUID());
    }
}
