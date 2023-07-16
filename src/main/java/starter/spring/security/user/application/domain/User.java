package starter.spring.security.user.application.domain;

import lombok.Getter;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2023/07/16
 */

@Getter
public class User {
    private final UserId id;
    private final String fullName;
    private final Email email;
    private String password;

    public User(UserId id, String fullName, Email email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public User(String fullName, Email email, String password) {
        this(new UserId(UUID.randomUUID()), fullName, email, password);
    }
}
