package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public final class OpenPolicyAgentAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final Logger logger = LoggerFactory.getLogger(OpenPolicyAgentAuthorizationManager.class);

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        // make request to Open Policy Agent
        if (authentication != null) {
            Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                logger.info("OpenPolicyAgentAuthorizationManager {}", auth);
                return new AuthorizationDecision(true);
            }
        }
        throw new AuthenticationCredentialsNotFoundException(
                "An Authentication object was not found in the SecurityContext");
    }
}