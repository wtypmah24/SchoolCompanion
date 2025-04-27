package org.back.beobachtungapp.resolver;

import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.companion.CompanionAuthentication;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class CurrentCompanionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentCompanion.class)
                && Companion.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof CompanionAuthentication companionAuth) {
            return companionAuth.getPrincipal();
        }

        throw new IllegalStateException("No authenticated Companion found");
    }
}