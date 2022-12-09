package starter.springsecurity.domain.authentication.model;

import lombok.AccessLevel;
import lombok.Getter;
import starter.springsecurity.domain.entity.BaseTimeEntity;
import starter.springsecurity.domain.entity.converter.BooleanConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Table(name = "code_auth")
@Getter
public abstract class CodeAuth extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID uuid;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "authenticated_yn", length = 1)
    @Convert(converter = BooleanConverter.class)
    @Getter(AccessLevel.PROTECTED)
    private Boolean authenticated;

    @Column(name = "expiration_time")
    @Getter(AccessLevel.PROTECTED)
    private LocalDateTime expirationTimeUTC;

    protected CodeAuth() {
        this.uuid = UUID.randomUUID();
        this.authenticated = false;
    }

    public void updateAuth(String verificationCode, int validMin) {
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

    public void passAuthentication() {
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
