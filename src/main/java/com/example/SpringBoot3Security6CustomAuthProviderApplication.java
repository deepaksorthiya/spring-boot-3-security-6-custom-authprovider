package com.example;

import com.example.security.CustomJdbcUserDetailManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class SpringBoot3Security6CustomAuthProviderApplication implements ApplicationRunner {

    final PasswordEncoder passwordEncoder;
    final UserDetailsService userDetailsService;
    final AuthenticationConfiguration authenticationConfiguration;
    private final List<AuthorizationManager> authorizationManagers;

    public SpringBoot3Security6CustomAuthProviderApplication(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, AuthenticationConfiguration authenticationConfiguration, List<AuthorizationManager> authorizationManagers) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.authorizationManagers = authorizationManagers;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3Security6CustomAuthProviderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CustomJdbcUserDetailManager customJdbcUserDetailManager = (CustomJdbcUserDetailManager) userDetailsService;
        customJdbcUserDetailManager.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        System.out.println("User : " + passwordEncoder.encode("password"));
        System.out.println("Admin : " + passwordEncoder.encode("admin"));
        System.out.println(customJdbcUserDetailManager);
        System.out.println(authenticationConfiguration);
        System.out.println(authorizationManagers);
    }
}
