package starter.spring.security.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import starter.spring.security.web.security.filter.JwtAuthenticationFilter;
import starter.spring.security.web.security.provider.JwtAuthenticationProvider;
import starter.spring.security.web.security.provider.MonitoringAuthenticationProvider;
import starter.spring.security.web.security.filter.UnauthorizedExceptionFilter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper                     objectMapper;
    private final JwtAuthenticationProvider        jwtAuthenticationProvider;
    private final MonitoringAuthenticationProvider monitoringAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
        UnauthorizedExceptionFilter unauthorizedExceptionFilter = new UnauthorizedExceptionFilter(objectMapper);

        http
                .csrf().disable()
                .authorizeRequests()
//                .antMatchers("/authentication/phone/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(unauthorizedExceptionFilter, JwtAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(jwtAuthenticationProvider);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain monitoringFilterChain(HttpSecurity http) throws Exception {
        http
                .antMatcher("/actuator/**")
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .and()
                .httpBasic()
                .realmName("Application Monitoring")
                .and()
                .authenticationProvider(monitoringAuthenticationProvider);

        return http.build();
    }
}
