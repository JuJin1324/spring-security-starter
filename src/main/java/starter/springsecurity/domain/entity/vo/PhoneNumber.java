package starter.springsecurity.domain.entity.vo;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
    public static final int VALID_NUMBER_LENGTH = 11;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "phone_no")
    private String phoneNo;
}
