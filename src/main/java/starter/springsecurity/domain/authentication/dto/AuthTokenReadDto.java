package starter.springsecurity.domain.authentication.dto;

import lombok.Getter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */
@Getter
public class AuthTokenReadDto {
    private final String accessToken;
    private final String refreshToken;

    public AuthTokenReadDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
