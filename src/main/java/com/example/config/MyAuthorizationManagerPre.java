package com.example.config;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class MyAuthorizationManagerPre implements AuthorizationManager<MethodInvocation> {

    private final Logger logger = LoggerFactory.getLogger(MyAuthorizationManagerPre.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        // ... authorization logic
        logger.info("MyAuthorizationManagerPre {}", authentication.get());
        return new AuthorizationDecision(true);
    }
}