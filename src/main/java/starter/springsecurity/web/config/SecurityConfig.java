package starter.springsecurity.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;
import starter.springsecurity.web.filter.JwtAuthenticationFilter;
import starter.springsecurity.web.filter.UnauthorizedExceptionFilter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final RegistrationTokenService registrationTokenService;
    private final AuthTokenService         authTokenService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/authentication/phone/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(registrationTokenService, authTokenService);
        UnauthorizedExceptionFilter unauthorizedExceptionFilter = new UnauthorizedExceptionFilter(objectMapper());

        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(unauthorizedExceptionFilter, JwtAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // TODO: http Basic for prometheus
        //                 .antMatchers("/actuator/**").hasRole("ROLE_ADMIN")

        return http.build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
