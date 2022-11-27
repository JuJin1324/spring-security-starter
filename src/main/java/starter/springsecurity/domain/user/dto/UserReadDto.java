package starter.springsecurity.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.user.model.User;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@Getter
public class UserReadDto {
    private UUID userId;

    @JsonUnwrapped
    private PhoneNumber phoneNumber;

    private String nickname;

    public UserReadDto(User user) {
        this.userId = user.getUuid();
        this.phoneNumber = user.getPhoneNumber();
        this.nickname = user.getNickname();
    }
}
