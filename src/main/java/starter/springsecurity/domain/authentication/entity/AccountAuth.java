package starter.springsecurity.domain.authentication.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Table(name = "account_auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AccountAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "password")
    private String password;

    protected AccountAuth(String loginName, String password) {
        this.uuid = UUID.randomUUID();
        this.loginName = loginName;
        this.password = password;
    }
}
