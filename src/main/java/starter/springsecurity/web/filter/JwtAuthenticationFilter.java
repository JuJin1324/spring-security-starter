package starter.springsecurity.web.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import starter.springsecurity.domain.token.auth.model.TokenType;
import starter.springsecurity.domain.token.auth.service.AuthTokenService;
import starter.springsecurity.domain.token.registration.service.RegistrationTokenService;
import starter.springsecurity.web.exception.InvalidJsonWebTokenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String CREATE_USER_URI = "/users";
    private static final String GET_AUTH_TOKEN_URI = "/authentication/token";

    private static final String TOKEN_PREFIX    = "Bearer ";

    private final RegistrationTokenService registrationTokenService;
    private final AuthTokenService         authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        Authentication authentication;
        if (hasRegistrationToken(request)) {
            UUID authId = registrationTokenService.getAuthId(token);
            authentication = new UsernamePasswordAuthenticationToken(authId, null, null);
            log.debug("Request has registration token, authId: {}", authId);
        } else if (hasRefreshToken(request)) {
            UUID userId = authTokenService.getUserId(token, TokenType.REFRESH);
            authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
            log.debug("Request has refresh token, userId: {}", userId);
        } else {
            UUID userId = authTokenService.getUserId(token, TokenType.ACCESS);
            // TODO: authorities 에 ROLE_USER 넣기
            authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
            log.debug("Request has access token, userId: {}", userId);
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

    private boolean hasRegistrationToken(HttpServletRequest request) {
        return isCreateUserRequest(request) || isGetAuthTokenRequest(request);
    }

    private boolean hasRefreshToken(HttpServletRequest request) {
        return isGetUpdatedAuthTokenRequest(request);
    }

    private boolean isCreateUserRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.POST.name())
                && request.getRequestURI().equals(CREATE_USER_URI);
    }

    private boolean isGetAuthTokenRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())
                && request.getRequestURI().equals(GET_AUTH_TOKEN_URI)
                && ObjectUtils.isEmpty(request.getParameter("updated"));
    }

    private boolean isGetUpdatedAuthTokenRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())
                && request.getRequestURI().equals(GET_AUTH_TOKEN_URI)
                && StringUtils.hasText(request.getParameter("updated"));
    }
}
