package starter.spring.security.user.application.port.in;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class IssuedAccessToken {
    private final String value;
}
