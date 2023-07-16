package starter.spring.security.core.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2022/07/06
 * Copyright (C) 2022, Centum Factorial all rights reserved.
 */

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class PhoneNumber {
    @Transient
    public static final int VALID_NUMBER_LENGTH = 11;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "phone_no")
    private String phoneNo;
}
