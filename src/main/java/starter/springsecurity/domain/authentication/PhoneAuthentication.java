package starter.springsecurity.domain.authentication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import starter.springsecurity.domain.vo.PhoneNumber;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

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
public class PhoneAuthentication extends CodeAuthentication {
    @Embedded
    private PhoneNumber phoneNumber;

    public PhoneAuthentication(PhoneNumber phoneNumber) {
        super();
        this.phoneNumber = phoneNumber;
    }
}
