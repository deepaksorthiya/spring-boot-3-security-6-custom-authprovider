package com.example.security;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class LocalizedThrowingMethodAuthorizationDeniedHandler implements MethodAuthorizationDeniedHandler {
    private final MessageSourceAccessor messages;

    public LocalizedThrowingMethodAuthorizationDeniedHandler(MessageSource messages) {
        this.messages = new MessageSourceAccessor(messages);
    }

    @Override
    public Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        String message = this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access Denied");
        throw new AuthorizationDeniedException(message);
    }
}
