package starter.spring.security.user.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Getter
@EqualsAndHashCode
public class UserId {
    private final UUID value;

    public UserId(UUID value) {
        this.value = value;
    }

    public UserId(String value) {
        this.value = UUID.fromString(value);
    }
}
