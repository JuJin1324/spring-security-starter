package starter.springsecurity.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.authentication.model.PhoneAuth;
import starter.springsecurity.domain.authentication.repository.PhoneAuthRepository;
import starter.springsecurity.domain.authentication.service.AuthenticationService;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.token.auth.model.TokenType;
import starter.springsecurity.domain.token.auth.repository.RefreshTokenRepository;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;
import starter.springsecurity.domain.user.dto.UserCreateDto;
import starter.springsecurity.domain.user.repository.UserRepository;
import starter.springsecurity.domain.user.service.UserService;
import starter.springsecurity.web.controller.AuthenticationController.CreatePhoneAuthResponse;
import starter.springsecurity.web.controller.AuthenticationController.VerifyPhoneAuthRequest;
import starter.springsecurity.web.controller.AuthenticationController.VerifyPhoneAuthResponse;

import java.util.TimeZone;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {
    static final String CREATE_PHONE_AUTH_URI      = "/authentication/phone";
    static final String VERIFY_PHONE_AUTH_URI      = "/authentication/phone?verify=true";
    static final String GET_AUTH_TOKEN_URI         = "/authentication/token";
    static final String GET_UPDATED_AUTH_TOKEN_URI = "/authentication/token?updated=true";

    static final String COUNTRY_CODE = "82";
    static final String PHONE_NO     = "01012344321";

    @Autowired
    PhoneAuthRepository    phoneAuthRepository;
    @Autowired
    UserRepository         userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    AuthenticationService    authenticationService;
    @Autowired
    RegistrationTokenService registrationTokenService;
    @Autowired
    UserService              userService;
    @Autowired
    AuthTokenService         authTokenService;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc      mockMvc;

    @BeforeEach
    void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    void tearDown() {
        phoneAuthRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[전화번호 인증 생성] 정상")
    void createPhoneAuth_whenNormal_thenReturnCreated() throws Exception {
        /* given */
        String countryCode = COUNTRY_CODE;
        String phoneNo = PHONE_NO;

        /* when */
        PhoneAuthCreateDto createDto = new PhoneAuthCreateDto(countryCode, phoneNo);
        MvcResult result = mockMvc.perform(post(CREATE_PHONE_AUTH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        /* then */
        CreatePhoneAuthResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), CreatePhoneAuthResponse.class);
        UUID authId = response.getAuthId();

        PhoneAuth phoneAuth = phoneAuthRepository.findByUuid(authId).get();
        PhoneNumber phoneNumber = phoneAuth.getPhoneNumber();
        assertEquals(countryCode, phoneNumber.getCountryCode());
        assertEquals(phoneNo, phoneNumber.getPhoneNo());
        assertFalse(phoneAuth.hasAuthenticated());
    }

    @Test
    @DisplayName("[전화번호 인증 검증] 정상")
    void verifyPhoneAuth_whenNormal_thenReturnToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);

        UUID authId = authenticationService.createPhoneAuth(phoneNumber);
        PhoneAuth phoneAuth = phoneAuthRepository.findByUuid(authId).get();
        String verificationCode = phoneAuth.getVerificationCode();

        /* when */
        VerifyPhoneAuthRequest request = new VerifyPhoneAuthRequest(authId, verificationCode);
        MvcResult result = mockMvc.perform(post(VERIFY_PHONE_AUTH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        VerifyPhoneAuthResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), VerifyPhoneAuthResponse.class);

        UUID authIdFromRegistrationToken = registrationTokenService.getAuthId(response.getRegistrationToken());
        assertEquals(authId, authIdFromRegistrationToken);
    }

    @Test
    @DisplayName("[인증 토큰 조회] 유효하지 않은 Registration token")
    void getAuthToken_whenInvalidRegistrationToken_thenStatusIsUnauthorized() throws Exception {
        /* given */
        String invalidRegistrationToken = "Invalid RegistrationToken";

        /* when */
        /* then */
        mockMvc.perform(get(GET_AUTH_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidRegistrationToken))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("[인증 토큰 조회] 정상")
    void getAuthToken_whenNormal_thenReturnAuthToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        String registrationToken = givenRegistrationToken(phoneNumber);

        UUID userId = userService.createUser(phoneNumber, new UserCreateDto("nickname test"));

        /* when */
        MvcResult result = mockMvc.perform(get(GET_AUTH_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + registrationToken))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        AuthTokenReadDto response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AuthTokenReadDto.class);

        String accessToken = response.getAccessToken();
        assertTrue(authTokenService.isUserIdMatchedWithToken(accessToken, userId));
        assertEquals(userId, authTokenService.getUserId(accessToken, TokenType.ACCESS));

        String refreshToken = response.getRefreshToken();
        assertTrue(refreshTokenRepository.findByToken(refreshToken).isPresent());
    }

    @Test
    @DisplayName("[업데이트된 인증 토큰 조회] 유효하지 않은 Refresh token")
    void getUpdatedAuthToken_whenInvalidRefreshToken_thenStatusIsUnauthorized() throws Exception {
        /* given */
        String invalidRefreshToken = "Invalid RefreshToken";

        /* when */
        /* then */
        mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidRefreshToken))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("[업데이트된 인증 토큰 조회] 정상")
    void getUpdatedAuthToken_whenNormal_thenReturnAuthToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        UUID userId = userService.createUser(phoneNumber, new UserCreateDto("nickname test"));
        AuthTokenReadDto authToken = authTokenService.createAuthToken(userId);
        /*
         * JWT 생성 시 issuedAt 으로 받는 timestamp 가 초까지 정보만 담고 있기 때문에
         * accessToken 과 newAccessToken 에 들어가는 issuedAt 의 timestamp 에 차이가 존재하지 않게 된다.
         * 그래서 authTokenService.createAuthToken() 메서드 실행 후 Thread.sleep(1000); 로 1초 기다린 후에 로직을 진행한다.
         */
        Thread.sleep(1000);

        /* when */
        MvcResult result = mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getRefreshToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        AuthTokenReadDto response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AuthTokenReadDto.class);

        String newAccessToken = response.getAccessToken();
        assertTrue(authTokenService.isUserIdMatchedWithToken(newAccessToken, userId));
        assertEquals(userId, authTokenService.getUserId(newAccessToken, TokenType.ACCESS));
        assertNotEquals(authToken.getAccessToken(), newAccessToken);
    }

    private String givenRegistrationToken(PhoneNumber phoneNumber) throws Exception {
        UUID authId = authenticationService.createPhoneAuth(phoneNumber);

        PhoneAuth phoneAuth = phoneAuthRepository.findByUuid(authId).get();
        String verificationCode = phoneAuth.getVerificationCode();
        authenticationService.verifyPhoneAuth(authId, verificationCode);

        VerifyPhoneAuthRequest request = new VerifyPhoneAuthRequest(authId, verificationCode);
        MvcResult result = mockMvc.perform(post(VERIFY_PHONE_AUTH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        /* then */
        VerifyPhoneAuthResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), VerifyPhoneAuthResponse.class);

        return response.getRegistrationToken();
    }
}
