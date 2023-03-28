package starter.spring.security.global.resolver.argument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import starter.spring.security.global.security.filter.AccessTokenAuthenticationToken;

import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin@100fac.com)
 * Created Date : 2021/09/30
 * Copyright (C) 2021, Centum Factorial all rights reserved.
 */

@Slf4j
public class AuthenticatedArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthenticatedAnnotation =
                parameter.hasParameterAnnotation(Authenticated.class);
        boolean hasUserType = UUID.class.isAssignableFrom(parameter.getParameterType());
        return hasAuthenticatedAnnotation && hasUserType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        AccessTokenAuthenticationToken authentication = (AccessTokenAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserId();
    }
}
