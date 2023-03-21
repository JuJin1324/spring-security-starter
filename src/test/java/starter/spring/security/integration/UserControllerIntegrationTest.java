package starter.spring.security.integration;

import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import starter.spring.security.domain.user.dto.UserCreateDto;
import starter.spring.security.domain.user.dto.UserReadDto;
import starter.spring.security.domain.user.model.User;
import starter.spring.security.web.controller.UserController;
import starter.spring.security.domain.authentication.dto.AccessToken;
import starter.spring.security.domain.entity.vo.PhoneNumber;

import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/09
 */

public class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {
    static final String CREATE_USER_URI     = "/users";
    static final String GET_SINGLE_USER_URI = "/users/{userId}";

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
    @DisplayName("[회원 생성] 유효하지 않은 Registration token")
    void createUser_whenInvalidRegistrationToken_thenStatusIsUnauthorized() throws Exception {
        /* given */
        String invalidRegistrationToken = "Invalid RegistrationToken";

        /* when */
        /* then */
        mockMvc.perform(post(CREATE_USER_URI)
                        .header(AUTHORIZATION, "Bearer " + invalidRegistrationToken))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("[회원 생성] 정상")
    void createUser_whenNormal_thenReturnUserId() throws Exception {
        /* given */
        /* when */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);

        UserCreateDto createDto = new UserCreateDto(nickname);
        MvcResult result = mockMvc.perform(post(CREATE_USER_URI)
                        .header(AUTHORIZATION, "Bearer " + givenRegistrationToken(phoneNumber))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andDo(print())
                .andReturn();

        /* then */
        UserController.CreateUserResponse response = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserController.CreateUserResponse.class);
        UUID userId = response.getUserId();

        User foundUser = userRepository.findByUuid(userId).get();
        assertEquals(nickname, foundUser.getNickname());
        assertEquals(phoneNumber, foundUser.getPhoneNumber());
    }

    @TestFactory
    @DisplayName("[회원 단건 조회] 유효하지 않은 Access token")
    Stream<DynamicTest> getSingleUser_whenInvalidAccessToken_thenStatusIsUnauthorized() {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AccessToken authToken = givenAuthToken(user);

        /* when */
        String invalidAccessToken = "invalid access token";
        String registrationToken = givenRegistrationToken(phoneNumber);
        String refreshToken = authToken.getRefreshToken();

        /* then */
        return Stream.of(
                dynamicTest("유효하지 않은 Access token", () -> {
                    mockMvc.perform(get(GET_SINGLE_USER_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + invalidAccessToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Access token 대신에 Registration token", () -> {
                    mockMvc.perform(get(GET_SINGLE_USER_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + registrationToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("Access token 대신에 Refresh token", () -> {
                    mockMvc.perform(get(GET_SINGLE_USER_URI, UUID.randomUUID().toString())
                                    .header(AUTHORIZATION, "Bearer " + refreshToken))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[회원 단건 조회] 정상")
    void getSingleUser_whenNormal_thenReturnUserReadDto() throws Exception {
        /* given */
        String nickname = "nickname test";
        PhoneNumber phoneNumber = new PhoneNumber(COUNTRY_CODE, PHONE_NO);
        User user = givenUser(phoneNumber, nickname);
        AccessToken authToken = givenAuthToken(user);

        /* when */
        UUID userId = user.getUuid();
        MvcResult result = mockMvc.perform(get(GET_SINGLE_USER_URI, userId)
                        .header(AUTHORIZATION, "Bearer " + authToken.getAccessToken()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        /* then */
        UserReadDto readDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserReadDto.class);

        assertEquals(user.getUuid(), readDto.getUserId());
        assertEquals(user.getNickname(), readDto.getNickname());
        assertEquals(user.getPhoneNumber(), readDto.getPhoneNumber());
    }
}
