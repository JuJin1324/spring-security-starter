package starter.springsecurity.web.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import starter.springsecurity.domain.token.auth.AuthTokenService;
import starter.springsecurity.domain.token.registration.RegistrationTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String CREATE_USER_URI = "/users";
    private static final String TOKEN_PREFIX    = "Bearer ";

    private final RegistrationTokenService registrationTokenService;
    private final AuthTokenService         authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        Authentication authentication;
        if (isCreateUser(request)) {
            UUID authId = registrationTokenService.getAuthId(token);
            authentication = new UsernamePasswordAuthenticationToken(authId, null);
        } else {
            UUID userId = authTokenService.getUserId(token);
            authentication = new UsernamePasswordAuthenticationToken(userId, null);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(bearerToken) || !bearerToken.startsWith(TOKEN_PREFIX)) {
            throw new InvalidJsonWebTokenException("Missed \"Bearer \" prefix.");
        }
        String jwt = bearerToken.substring(TOKEN_PREFIX.length());
        if (ObjectUtils.isEmpty(jwt)) {
            throw new InvalidJsonWebTokenException("JWT is empty.");
        }
        return jwt;
    }

    private boolean isCreateUser(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())
                && request.getRequestURI().equals(CREATE_USER_URI);
    }
}
