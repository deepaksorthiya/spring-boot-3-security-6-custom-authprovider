package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class MyAuthorizationManagerPost implements AuthorizationManager<MethodInvocationResult> {

    private final Logger logger = LoggerFactory.getLogger(MyAuthorizationManagerPost.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocationResult methodInvocationResult) {
        // ... authorization logic
        logger.info("MyAuthorizationManagerPost {}", authentication.get());
        return new AuthorizationDecision(true);
    }
}