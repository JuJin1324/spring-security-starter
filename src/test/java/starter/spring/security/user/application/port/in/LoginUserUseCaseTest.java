package starter.spring.security.user.application.port.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import starter.spring.security.user.application.domain.Email;
import starter.spring.security.user.application.port.out.IssueAccessTokenPort;
import starter.spring.security.user.application.service.MockLoginUserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {
    private LoginUserUseCase loginUserUseCase;

    @Mock
    private IssueAccessTokenPort issueAccessTokenPort;

    @BeforeEach
    void setUp() {
        loginUserUseCase = new MockLoginUserService(issueAccessTokenPort);
    }

    @Test
    @DisplayName("로그인이 성공적으로 되면 발급된 엑세스 토큰 반환")
    void whenLoginSuccessfullyThenReturnIssuedAccessToken() {
        /* given */
        Email email = new Email("mock@gmail.com");
        String password = "password";
        IssuedAccessToken expected = new IssuedAccessToken("accessTokenValue");
        given(issueAccessTokenPort.issueNewOne(any())).willReturn(expected);

        /* when */
        IssuedAccessToken accessToken = loginUserUseCase.login(new LoginUserCommand(email, password));

        /* then */
        assertEquals(expected, accessToken);
    }
}
