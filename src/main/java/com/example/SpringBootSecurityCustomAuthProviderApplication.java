package com.example;

import com.example.security.CustomJdbcUserDetailManager;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.List;

@SpringBootApplication
@ImportRuntimeHints(SpringBootSecurityCustomAuthProviderApplication.ExamplesRuntimeHints.class)
public class SpringBootSecurityCustomAuthProviderApplication implements ApplicationRunner {

    final PasswordEncoder passwordEncoder;
    final UserDetailsService userDetailsService;
    final AuthenticationConfiguration authenticationConfiguration;
    private final List<AuthorizationManager> authorizationManagers;

    public SpringBootSecurityCustomAuthProviderApplication(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, AuthenticationConfiguration authenticationConfiguration, List<AuthorizationManager> authorizationManagers) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.authorizationManagers = authorizationManagers;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecurityCustomAuthProviderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CustomJdbcUserDetailManager customJdbcUserDetailManager = (CustomJdbcUserDetailManager) userDetailsService;
        System.out.println("User : " + passwordEncoder.encode("password"));
        System.out.println("Admin : " + passwordEncoder.encode("admin"));
        System.out.println(customJdbcUserDetailManager);
        System.out.println(authenticationConfiguration.getAuthenticationManager());
        System.out.println(authorizationManagers);
    }

    /**
     * For native build below config is required for serializing {@link org.springframework.security.core.Authentication}.
     * Test api/authentication. It will break without this config.
     * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
     * which use the {@link WebAuthenticationDetails}
     */
    static class ExamplesRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection()
                    .registerType(WebAuthenticationDetails.class, MemberCategory.values());
        }

    }
}
