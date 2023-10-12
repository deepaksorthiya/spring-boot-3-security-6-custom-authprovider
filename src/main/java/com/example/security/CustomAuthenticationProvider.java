package com.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author deepakk
 * @date Sep 9, 2019
 */
@Slf4j
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // process additional request parameters here
        final String verificationCode = ((CustomWebAuthenticationDetails) authentication.getDetails())
                .getVerificationCode();
        log.info("Verification Code : " + verificationCode);
        return super.authenticate(authentication);
    }
}
