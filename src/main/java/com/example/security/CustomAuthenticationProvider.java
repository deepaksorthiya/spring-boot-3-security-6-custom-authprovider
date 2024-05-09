package com.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Objects;

@Slf4j
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // process additional request parameters here
        String verificationCode = null;
        if (Objects.nonNull(authentication.getDetails())) {
            verificationCode = ((CustomWebAuthenticationDetails) authentication.getDetails())
                    .getVerificationCode();
        }
        log.info("Verification Code {} ", verificationCode);
        return super.authenticate(authentication);
    }
}
