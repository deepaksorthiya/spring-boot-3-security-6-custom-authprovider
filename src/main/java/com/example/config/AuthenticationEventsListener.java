package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsListener {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationEventsListener.class);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        // ... implement logic
        logger.info("AuthenticationSuccessEvent {}", success);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        // ... implement logic
        logger.info("AbstractAuthenticationFailureEvent {}", failures);
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent logoutSuccessEvent) {
        // ... implement logic
        logger.info("LogoutSuccessEvent {}", logoutSuccessEvent);
    }
}