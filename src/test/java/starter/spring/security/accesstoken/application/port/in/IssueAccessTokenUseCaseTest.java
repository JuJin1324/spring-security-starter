package starter.spring.security.accesstoken.application.port.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import starter.spring.security.accesstoken.application.domain.AccessToken;
import starter.spring.security.accesstoken.application.port.out.IssueAccessTokenPort;
import starter.spring.security.accesstoken.application.service.IssueAccessTokenService;
import starter.spring.security.user.application.domain.UserId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@ExtendWith(MockitoExtension.class)
class IssueAccessTokenUseCaseTest {
    private IssueAccessTokenUseCase issueAccessTokenUseCase;

    @Mock
    private IssueAccessTokenPort issueAccessTokenPort;

    @BeforeEach
    void setUp() {
        issueAccessTokenUseCase = new IssueAccessTokenService(issueAccessTokenPort);
    }

    @Test
    @DisplayName("토큰 발급 시 엑세스 토큰 반환")
    void whenIssueNewOneThenReturnAccessToken() {
        /* given */
        UserId userId = givenUserId();
        AccessToken expected = new AccessToken("accessTokenValue", userId);
        given(issueAccessTokenPort.issueNewOne(userId))
                .willReturn(expected);

        /* when */
        AccessToken accessToken = issueAccessTokenUseCase.issueNewOne(userId);

        /* then */
        assertEquals(expected.getUserId(), accessToken.getUserId());
        assertEquals(expected.getValue(), accessToken.getValue());
    }

    private UserId givenUserId() {
        return new UserId(UUID.randomUUID());
    }
}
