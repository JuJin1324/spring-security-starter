package starter.spring.security.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import starter.spring.security.global.security.filter.AccessTokenAuthenticationFilter;
import starter.spring.security.global.security.provider.AccessTokenAuthenticationProvider;
import starter.spring.security.global.security.provider.MonitoringAuthenticationProvider;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;
    private final MonitoringAuthenticationProvider  monitoringAuthenticationProvider;

    @Bean
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        AccessTokenAuthenticationFilter accessTokenAuthenticationFilter = new AccessTokenAuthenticationFilter();

        http.authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers(
                        "/authentications/phone/**",
                        "/authentications/access-token/**"
                ).permitAll()
                .and()
                .addFilterBefore(accessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(accessTokenAuthenticationProvider)
                .csrf().disable();

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain monitoringFilterChain(HttpSecurity http) throws Exception {
        http.antMatcher("/actuator/**")
                .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .and()
                .httpBasic()
                .realmName("Application Monitoring")
                .and()
                .authenticationProvider(monitoringAuthenticationProvider)
                .csrf().disable();

        return http.build();
    }
}
