package starter.spring.security.domain.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import starter.spring.security.global.entity.vo.PhoneNumber;

import javax.validation.constraints.NotBlank;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/23
 */

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class PhoneAuthenticationCreateDto {
    @NotBlank
    private String countryCode;
    @NotBlank
    private String phoneNo;

    @JsonIgnore
    public PhoneNumber getPhoneNumber() {
        return new PhoneNumber(this.countryCode, this.phoneNo);
    }
}
