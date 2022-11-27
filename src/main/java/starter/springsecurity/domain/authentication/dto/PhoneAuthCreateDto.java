package starter.springsecurity.domain.authentication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import starter.springsecurity.domain.entity.vo.PhoneNumber;

import javax.validation.constraints.NotBlank;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
public class PhoneAuthCreateDto {
    @NotBlank
    private String countryCode;
    @NotBlank
    private String phoneNo;

    public PhoneNumber getPhoneNumber() {
        return new PhoneNumber(this.countryCode, this.phoneNo);
    }
}
