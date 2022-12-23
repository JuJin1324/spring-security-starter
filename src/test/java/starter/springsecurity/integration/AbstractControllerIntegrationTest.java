package starter.springsecurity.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.entity.PhoneAuth;
import starter.springsecurity.domain.authentication.repository.PhoneAuthRepository;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;
import starter.springsecurity.domain.user.model.User;
import starter.springsecurity.domain.user.repository.UserRepository;

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
    PhoneAuthRepository phoneAuthRepository;
    @Autowired
    UserRepository      userRepository;

    @Autowired
    AuthenticationService    authenticationService;
    @Autowired
    RegistrationTokenService registrationTokenService;
    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc      mockMvc;

    protected String givenRegistrationToken(PhoneNumber phoneNumber) {
        UUID authId = authenticationService.createPhoneAuth(phoneNumber);

        PhoneAuth phoneAuth = phoneAuthRepository.findByUuid(authId).get();
        String verificationCode = phoneAuth.getVerificationCode();
        authenticationService.verifyPhoneAuth(authId, verificationCode);
        return registrationTokenService.createRegistrationToken(authId);
    }

    protected User givenUser(PhoneNumber phoneNumber, String nickname) {
        return userRepository.save(new User(phoneNumber, nickname));
    }

    protected AuthTokenReadDto givenAuthToken(User user) {
        return authTokenService.createAuthToken(user.getUuid());
    }
}
