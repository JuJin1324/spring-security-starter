package starter.spring.security.domain.authentication.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessToken {
    private String accessToken;
    private String refreshToken;

    public AccessToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
