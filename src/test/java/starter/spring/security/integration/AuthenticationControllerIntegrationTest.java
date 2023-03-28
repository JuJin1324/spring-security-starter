package starter.spring.security.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import starter.spring.security.domain.user.entity.User;
import starter.spring.security.domain.authentication.controller.AuthenticationController;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.authentication.dto.PhoneAuthenticationCreateDto;
import starter.spring.security.domain.authentication.entity.PhoneAuthentication;
import starter.spring.security.global.entity.vo.PhoneNumber;
import starter.spring.security.domain.token.repository.RefreshTokenRepository;
import starter.spring.security.domain.token.service.AccessTokenService;

import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/11/27
 */

class AuthenticationControllerIntegrationTest extends AbstractControllerIntegrationTest {
    static final String CREATE_PHONE_AUTH_URI      = "/authentication/phone";
    static final String VERIFY_PHONE_AUTH_URI      = "/authentication/phone?verify=true";
    static final String GET_AUTH_TOKEN_URI         = "/authentication/token";
    static final String GET_UPDATED_AUTH_TOKEN_URI = "/authentication/token?updated=true";

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    AccessTokenService accessTokenService;

    @BeforeEach
    void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @AfterEach
    void tearDown() {
        phoneAuthenticationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[전화번호 인증 생성] 정상")
    void createPhoneAuth_whenNormal_thenReturnCreated() throws Exception {
        /* given */
        String countryCode = COUNTRY_CODE;
        String phoneNo = PHONE_NO;

        /* when */
        PhoneAuthenticationCreateDto createDto = new PhoneAuthenticationCreateDto(countryCode, phoneNo);
        MvcResult result = mockMvc.perform(post(CREATE_PHONE_AUTH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        /* then */
        AuthenticationController.CreatePhoneAuthResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AuthenticationController.CreatePhoneAuthResponse.class);
        UUID authId = response.getAuthId();

        PhoneAuthentication phoneAuthentication = phoneAuthenticationRepository.findByUuid(authId).get();
        PhoneNumber phoneNumber = phoneAuthentication.getPhoneNumber();
        assertEquals(countryCode, phoneNumber.getCountryCode());
        assertEquals(phoneNo, phoneNumber.getPhoneNo());
        assertFalse(phoneAuthentication.hasAuthenticated());
    }

    @Test
    @DisplayName("[전화번호 인증 검증] 정상")
    void verifyPhoneAuth_whenNormal_thenReturnToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);

        UUID authId = authenticationService.createPhoneAuthentication(phoneNumber);
        PhoneAuthentication phoneAuthentication = phoneAuthenticationRepository.findByUuid(authId).get();
        String verificationCode = phoneAuthentication.getVerificationCode();

        /* when */
        AuthenticationController.VerifyPhoneAuthRequest request = new AuthenticationController.VerifyPhoneAuthRequest(authId, verificationCode);
        MvcResult result = mockMvc.perform(post(VERIFY_PHONE_AUTH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        AuthenticationController.VerifyPhoneAuthResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AuthenticationController.VerifyPhoneAuthResponse.class);

        UUID authIdFromRegistrationToken = registrationTokenService.getAuthId(response.getAuthenticationToken());
        assertEquals(authId, authIdFromRegistrationToken);
    }

    @TestFactory
    @DisplayName("[인증 토큰 조회] 유효하지 않은 Registration token")
    Stream<DynamicTest> getAuthToken_whenInvalidRegistrationToken_thenStatusIsUnauthorized() {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AccessToken authToken = givenAuthToken(user);

        /* when */
        String invalidRegistrationToken = "Invalid RegistrationToken";
        String accessToken = authToken.getAccessToken();
        String refreshToken = authToken.getRefreshToken();

        /* then */
        return Stream.of(
                dynamicTest("유효하지 않은 Registration token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + invalidRegistrationToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Registration token 대신에 Access token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + accessToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Registration token 대신에 Refresh token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + refreshToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[인증 토큰 조회] 정상")
    void getAuthToken_whenNormal_thenReturnAuthToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        String registrationToken = givenRegistrationToken(phoneNumber);
        User user = givenUser(phoneNumber, "nickname test");
        UUID userId = user.getUuid();

        /* when */
        MvcResult result = mockMvc.perform(get(GET_AUTH_TOKEN_URI)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + registrationToken))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        AccessToken response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AccessToken.class);

        String accessToken = response.getAccessToken();
        assertTrue(accessTokenService.isUserIdMatchedWithToken(accessToken, userId));
        assertEquals(userId, accessTokenService.getUserId(accessToken, TokenType.ACCESS));

        String refreshToken = response.getRefreshToken();
        assertTrue(refreshTokenRepository.findByToken(refreshToken).isPresent());
    }

    @TestFactory
    @DisplayName("[업데이트된 인증 토큰 조회] 유효하지 않은 Refresh token")
    Stream<DynamicTest> getUpdatedAuthToken_whenInvalidRefreshToken_thenStatusIsUnauthorized() {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AccessToken authToken = givenAuthToken(user);

        /* when */
        String invalidRefreshToken = "Invalid RefreshToken";
        String registrationToken = givenRegistrationToken(phoneNumber);
        String accessToken = authToken.getAccessToken();

        /* then */
        return Stream.of(
                dynamicTest("유효하지 않은 Refresh token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + invalidRefreshToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Refresh token 대신에 Registration token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + registrationToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Refresh token 대신에 Access token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + accessToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[업데이트된 인증 토큰 조회] 정상")
    void getUpdatedAuthToken_whenNormal_thenReturnAuthToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, "nickname test");
        UUID userId = user.getUuid();
        AccessToken authToken = accessTokenService.createAccessToken(userId);
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
        AccessToken response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AccessToken.class);

        String newAccessToken = response.getAccessToken();
        assertTrue(accessTokenService.isUserIdMatchedWithToken(newAccessToken, userId));
        assertEquals(userId, accessTokenService.getUserId(newAccessToken, TokenType.ACCESS));
        assertNotEquals(authToken.getAccessToken(), newAccessToken);
    }
}
