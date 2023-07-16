package starter.spring.security.springconfig.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import starter.spring.security.accesstoken.adapter.in.security.AccessTokenAuthenticationProvider;

import static org.springframework.http.HttpMethod.POST;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

//@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;
    private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;

    private final HttpBasicAuthenticationProvider httpBasicAuthenticationProvider;

    @Bean
    public SecurityFilterChain bearerTokenFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers(POST, "/users/login").permitAll()
                .antMatchers(POST, "/users").permitAll()
                .and()
                .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(accessTokenAuthenticationProvider)
                .csrf().disable();

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain httpBasicFilterChain(HttpSecurity http) throws Exception {
        http.antMatcher("/actuator/**")
                .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .and()
                .httpBasic()
                .realmName("Application Monitoring")
                .and()
                .authenticationProvider(httpBasicAuthenticationProvider)
                .csrf().disable();

        return http.build();
    }
}
