package starter.spring.security.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import starter.spring.security.domain.authentication.repository.PhoneAuthenticationRepository;
import starter.spring.security.domain.authentication.service.AuthenticationService;
import starter.spring.security.domain.user.entity.User;
import starter.spring.security.domain.user.repository.UserRepository;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.authentication.entity.PhoneAuthentication;
import starter.spring.security.global.entity.vo.PhoneNumber;
import starter.spring.security.domain.token.service.AccessTokenService;
import starter.spring.security.domain.token.registration.service.RegistrationTokenService;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractControllerIntegrationTest {
    static final String COUNTRY_CODE = "82";
    static final String PHONE_NO     = "01012344321";

    @Autowired
    PhoneAuthenticationRepository phoneAuthenticationRepository;
    @Autowired
    UserRepository                userRepository;

    @Autowired
    AuthenticationService    authenticationService;
    @Autowired
    RegistrationTokenService registrationTokenService;
    @Autowired
    AccessTokenService       accessTokenService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc      mockMvc;

    protected String givenRegistrationToken(PhoneNumber phoneNumber) {
        UUID authId = authenticationService.createPhoneAuthentication(phoneNumber);

        PhoneAuthentication phoneAuthentication = phoneAuthenticationRepository.findByUuid(authId).get();
        String verificationCode = phoneAuthentication.getVerificationCode();
        authenticationService.verifyPhoneAuthentication(authId, verificationCode);
        return registrationTokenService.createRegistrationToken(authId);
    }

    protected User givenUser(PhoneNumber phoneNumber, String nickname) {
        return userRepository.save(new User(phoneNumber, nickname));
    }

    protected AccessToken givenAuthToken(User user) {
        return accessTokenService.createAccessToken(user.getUuid());
    }
}
