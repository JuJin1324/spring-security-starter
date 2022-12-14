package starter.springsecurity.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import starter.springsecurity.domain.authentication.dto.AuthTokenReadDto;
import starter.springsecurity.domain.authentication.dto.PhoneAuthCreateDto;
import starter.springsecurity.domain.authentication.entity.PhoneAuth;
import starter.springsecurity.domain.entity.vo.PhoneNumber;
import starter.springsecurity.domain.token.auth.entity.TokenType;
import starter.springsecurity.domain.token.auth.repository.RefreshTokenRepository;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.user.model.User;
import starter.springsecurity.web.controller.AuthenticationController.CreatePhoneAuthResponse;
import starter.springsecurity.web.controller.AuthenticationController.VerifyPhoneAuthRequest;
import starter.springsecurity.web.controller.AuthenticationController.VerifyPhoneAuthResponse;

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
    AuthTokenService authTokenService;

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
    @DisplayName("[???????????? ?????? ??????] ??????")
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
    @DisplayName("[???????????? ?????? ??????] ??????")
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

    @TestFactory
    @DisplayName("[?????? ?????? ??????] ???????????? ?????? Registration token")
    Stream<DynamicTest> getAuthToken_whenInvalidRegistrationToken_thenStatusIsUnauthorized() {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AuthTokenReadDto authToken = givenAuthToken(user);

        /* when */
        String invalidRegistrationToken = "Invalid RegistrationToken";
        String accessToken = authToken.getAccessToken();
        String refreshToken = authToken.getRefreshToken();

        /* then */
        return Stream.of(
                dynamicTest("???????????? ?????? Registration token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + invalidRegistrationToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Registration token ????????? Access token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + accessToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Registration token ????????? Refresh token", () -> {
                    mockMvc.perform(get(GET_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + refreshToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[?????? ?????? ??????] ??????")
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
        AuthTokenReadDto response = objectMapper
                .readValue(result.getResponse().getContentAsString(), AuthTokenReadDto.class);

        String accessToken = response.getAccessToken();
        assertTrue(authTokenService.isUserIdMatchedWithToken(accessToken, userId));
        assertEquals(userId, authTokenService.getUserId(accessToken, TokenType.ACCESS));

        String refreshToken = response.getRefreshToken();
        assertTrue(refreshTokenRepository.findByToken(refreshToken).isPresent());
    }

    @TestFactory
    @DisplayName("[??????????????? ?????? ?????? ??????] ???????????? ?????? Refresh token")
    Stream<DynamicTest> getUpdatedAuthToken_whenInvalidRefreshToken_thenStatusIsUnauthorized() {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AuthTokenReadDto authToken = givenAuthToken(user);

        /* when */
        String invalidRefreshToken = "Invalid RefreshToken";
        String registrationToken = givenRegistrationToken(phoneNumber);
        String accessToken = authToken.getAccessToken();

        /* then */
        return Stream.of(
                dynamicTest("???????????? ?????? Refresh token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + invalidRefreshToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Refresh token ????????? Registration token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + registrationToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Refresh token ????????? Access token", () -> {
                    mockMvc.perform(get(GET_UPDATED_AUTH_TOKEN_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + accessToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[??????????????? ?????? ?????? ??????] ??????")
    void getUpdatedAuthToken_whenNormal_thenReturnAuthToken() throws Exception {
        /* given */
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, "nickname test");
        UUID userId = user.getUuid();
        AuthTokenReadDto authToken = authTokenService.createAuthToken(userId);
        /*
         * JWT ?????? ??? issuedAt ?????? ?????? timestamp ??? ????????? ????????? ?????? ?????? ?????????
         * accessToken ??? newAccessToken ??? ???????????? issuedAt ??? timestamp ??? ????????? ???????????? ?????? ??????.
         * ????????? authTokenService.createAuthToken() ????????? ?????? ??? Thread.sleep(1000); ??? 1??? ????????? ?????? ????????? ????????????.
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
}
