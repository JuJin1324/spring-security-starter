package starter.springsecurity.integration;

import org.junit.jupiter.api.*;

import java.util.Base64;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/12/13
 */

public class SpringActuatorIntegrationTest extends AbstractControllerIntegrationTest {
    static final String ACTUATOR_HEALTH  = "/actuator/health";
    static final String ACTUATOR_INFO    = "/actuator/info";

    static final String HTTP_BASIC_USERNAME = "admin";
    static final String HTTP_BASIC_PASSWORD = "1234";

    @BeforeEach
    void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    @DisplayName("[Actuator health] 정상")
    void actuatorHealth_whenNormal() throws Exception {
        /* given */

        /* when */
        /* then */
        mockMvc.perform(get(ACTUATOR_HEALTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andDo(print());
    }

    @TestFactory
    @DisplayName("[Actuator info] 유효하지 않은 Authentication")
    Stream<DynamicTest> actuatorInfo_whenInvalidAuthentication_thenReturn() {
        /* given */
        String invalidUsername = "invalid";
        String invalidPassword = "password";

        /* when */
        String invalidUsernameAndPassword = getBasicAuthentication(invalidUsername, invalidPassword);
        String validUsernameAndInvalidPassword = getBasicAuthentication(HTTP_BASIC_USERNAME, invalidPassword);

        /* then */
        return Stream.of(
                dynamicTest("유효하지 않은 회원이름, 유효하지 않은 패스워드", () -> {
                    mockMvc.perform(get(ACTUATOR_INFO)
                                    .header(AUTHORIZATION, "Basic " + invalidUsernameAndPassword))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                }),
                dynamicTest("유효한 회원이름, 유효하지 않은 패스워드", () -> {
                    mockMvc.perform(get(ACTUATOR_INFO)
                                    .header(AUTHORIZATION, "Basic " + validUsernameAndInvalidPassword))
                            .andExpect(status().isUnauthorized())
                            .andDo(print());
                })
        );
    }

    @Test
    @DisplayName("[Actuator info] 유효한 basic authentication")
    void actuatorInfo_whenValidBasicAuthentication() throws Exception {
        /* given */

        /* when */
        String basicAuthentication = getBasicAuthentication(HTTP_BASIC_USERNAME, HTTP_BASIC_PASSWORD);

        /* then */
        mockMvc.perform(get(ACTUATOR_INFO)
                        .header(AUTHORIZATION, "Basic " + basicAuthentication))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private String getBasicAuthentication(String username, String password) {
        String authentication = username + ":" + password;
        return Base64.getEncoder().encodeToString(authentication.getBytes());
    }
}
