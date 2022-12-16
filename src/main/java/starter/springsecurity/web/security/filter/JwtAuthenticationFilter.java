package starter.springsecurity.web.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import starter.springsecurity.domain.token.auth.model.TokenType;
import starter.springsecurity.web.exception.InvalidJsonWebTokenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String CREATE_USER_URI    = "/users";
    private static final String GET_AUTH_TOKEN_URI = "/authentication/token";

    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jsonWebToken;
        TokenType tokenType;
        if (hasNoToken(request)) {
            jsonWebToken = null;
            tokenType = TokenType.NONE;
        } else if (hasRegistrationToken(request)) {
            jsonWebToken = getJwtFromRequest(request);
            tokenType = TokenType.REGISTRATION;
        } else if (hasRefreshToken(request)) {
            jsonWebToken = getJwtFromRequest(request);
            tokenType = TokenType.REFRESH;
        } else {
            jsonWebToken = getJwtFromRequest(request);
            tokenType = TokenType.ACCESS;
        }
        Authentication authentication = new JwtAuthenticationToken(jsonWebToken, tokenType);
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

    private boolean hasNoToken(HttpServletRequest request) {
        return ObjectUtils.isEmpty(request.getHeader(HttpHeaders.AUTHORIZATION));
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
