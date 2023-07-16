package starter.spring.security.user.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@RequiredArgsConstructor
@Getter
public class Email {
    private final String address;
}
