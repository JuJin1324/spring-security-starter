package starter.spring.security.domain.authentication.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2022/06/21
 * Copyright (C) 2022, Centum Factorial all rights reserved.
 */

@Entity
@DiscriminatorValue("PHONE")
@Table(name = "phone_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneAuthentication extends Authentication {
    public static final int EXPIRATION_MINUTE = 3;

    @Embedded
    private PhoneNumber phoneNumber;

    protected PhoneAuthentication(PhoneNumber phoneNumber, String verificationCode) {
        super(
                verificationCode,
                LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(EXPIRATION_MINUTE)
        );
        this.phoneNumber = phoneNumber;
    }

    public static PhoneAuthentication of(PhoneNumber phoneNumber, String verificationCode) {
        return new PhoneAuthentication(phoneNumber, verificationCode);
    }
}
