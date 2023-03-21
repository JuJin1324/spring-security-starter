package starter.spring.security.web.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import starter.spring.security.domain.token.auth.entity.TokenType;

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
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jsonWebToken = getJwtFrom(request);

        Authentication authentication = StringUtils.hasText(jsonWebToken) ?
                JwtAuthenticationToken.of(jsonWebToken, TokenType.ACCESS) :
                JwtAuthenticationToken.of(null, TokenType.NONE);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String getJwtFrom(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(bearerToken) || !bearerToken.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        String jwt = bearerToken.substring(TOKEN_PREFIX.length());
        if (ObjectUtils.isEmpty(jwt)) {
            return null;
        }
        return jwt;
    }
}
