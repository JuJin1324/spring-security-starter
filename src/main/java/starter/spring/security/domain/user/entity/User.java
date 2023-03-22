package starter.spring.security.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/20
 */

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID uuid;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(name = "nickname")
    private String nickname;

    public User(PhoneNumber phoneNumber, String nickname) {
        this.uuid = UUID.randomUUID();
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
    }
}
