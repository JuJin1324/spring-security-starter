package starter.spring.security.domain.authentication.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.spring.security.domain.entity.BaseTimeEntity;
import starter.spring.security.domain.entity.converter.BooleanConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2023/03/22
 * Copyright (C) 2023, Centum Factorial all rights reserved.
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Table(name = "authentication")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Authentication extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTimeUTC;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTimeUTC;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "verified")
    private Boolean verified;

    @Convert(converter = BooleanConverter.class)
    @Column(name = "expired")
    private Boolean expired;

    public Authentication(String verificationCode, LocalDateTime expirationTimeUTC) {
        this.verificationCode = verificationCode;
        this.createdTimeUTC = LocalDateTime.now(ZoneId.of("UTC"));
        this.expirationTimeUTC = expirationTimeUTC;
        this.verified = false;
        this.expired = false;
    }

    public boolean hasExpired() {
        return this.expirationTimeUTC.isBefore(LocalDateTime.now(ZoneId.of("UTC")))
                || this.expired;
    }

    public boolean hasMatchedVerificationCode(String verificationCode) {
        return this.verificationCode.equals(verificationCode);
    }

    public void passVerification() {
        this.verified = true;
        this.expired = false;
    }

    public void expireVerification() {
        this.expired = true;
    }
}
