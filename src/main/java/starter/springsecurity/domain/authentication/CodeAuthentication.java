package starter.springsecurity.domain.authentication;

import lombok.Getter;
import starter.springsecurity.domain.entity.BooleanConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Table(name = "code_auth")
@Getter
public abstract class CodeAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "authenticated_yn", length = 1)
    @Convert(converter = BooleanConverter.class)
    private Boolean authenticated;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTimeUTC;

    protected CodeAuthentication() {
        this.authenticated = false;
    }

    public void updateAuth(String verificationCode, Long validMin) {
        this.authenticated = false;
        this.verificationCode = verificationCode;
        this.expirationTimeUTC = LocalDateTime.now().plusMinutes(validMin);
    }

    public void initialize() {
        this.expirationTimeUTC = LocalDateTime.now().minusMinutes(30);
        this.authenticated = false;
    }

    public boolean verifyExpirationTime() {
        return LocalDateTime.now().isBefore(this.getExpirationTimeUTC());
    }

    public boolean verifyCode(String verificationCode) {
        return this.verificationCode.equals(verificationCode);
    }

    public void pass() {
        this.authenticated = true;
        this.expirationTimeUTC = LocalDateTime.now().plusMinutes(30);
    }

    public boolean hasAuthenticated() {
        /* 유효시간이 지나면 authenticated 가 true 여도 인증이 되지 않은 것으로 간주한다. */
        if (!this.verifyExpirationTime()) {
            return false;
        }
        return this.authenticated;
    }
}
