package starter.spring.security.springconfig.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/10/24
 */

@Component
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (ObjectUtils.isEmpty(authHeader)) {
            return null;
        }
        return AccessAuthenticationToken.of(authHeader);
    }
}
