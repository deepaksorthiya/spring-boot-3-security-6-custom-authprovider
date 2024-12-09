package com.example.config;

import com.example.security.CustomAuthenticationProvider;
import com.example.security.CustomJdbcUserDetailManager;
import com.example.security.CustomWebAuthenticationDetailsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.authorization.method.AuthorizationManagerAfterMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@EnableWebSecurity(/*debug = true*/)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource) throws Exception {
        http
                .formLogin(formLogin -> formLogin.authenticationDetailsSource(customWebAuthenticationDetailsSource)
                        .loginPage("/login").permitAll().defaultSuccessUrl("/home", true))
                .logout(logout -> logout.logoutUrl("/logout").permitAll())
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        //allow h2 console, all actuator endpoints and all static content
                        .requestMatchers(PathRequest.toH2Console(), PathRequest.toStaticResources().atCommonLocations(), EndpointRequest.toAnyEndpoint()).permitAll()
                        //allow server-info to access all
                        .requestMatchers("/server-info").permitAll()
                        //custom authorization manager
                        .requestMatchers("/user/**", "/admin/**", "/principal/**", "/authUser/**", "/change-password/**", "/home/**").access((authentication, object) -> {
                            // make request to Open Policy Agent
                            Authentication auth = authentication.get();
                            if (auth == null || !auth.isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            LOGGER.info("OpenPolicyAgentAuthorizationManager {}", auth);
                            return new AuthorizationDecision(true);
                        })
                        //rest of all request requires to be authenticated
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .anonymous(AbstractHttpConfigurer::disable)
        ;
        return http.build();
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(MessageSource messageSource, CustomJdbcUserDetailManager userDetailsService, CustomAuthenticationProvider authenticationProvider) {
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setMessageSource(messageSource);
        userDetailsService.setAuthenticationManager(providerManager);
        return providerManager;
    }


    @Bean(BeanIds.USER_DETAILS_SERVICE)
    public CustomJdbcUserDetailManager userDetailsServiceBean(MessageSource messageSource, DataSource dataSource) {
        CustomJdbcUserDetailManager userDetailsServiceBean = new CustomJdbcUserDetailManager(dataSource);
        userDetailsServiceBean.setMessageSource(messageSource);
        userDetailsServiceBean.setRolePrefix("ROLE_");
        return userDetailsServiceBean;
    }

    /**
     * This Custom Authorization Manager will call before spring's PreAuthorizeAuthorizationManager
     *
     * @see org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preAuthorize() {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize((authentication, object) -> {
            // ... authorization logic
            LOGGER.info("MyAuthorizationManagerPre {}", authentication.get());
            return new AuthorizationDecision(true);
        });
    }

    /**
     * This Custom Authorization Manager will call after spring's PostAuthorizeAuthorizationManager
     *
     * @see org.springframework.security.authorization.method.PostAuthorizeAuthorizationManager
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor postAuthorize() {
        return AuthorizationManagerAfterMethodInterceptor.postAuthorize((authentication, object) -> {
            // ... authorization logic
            LOGGER.info("MyAuthorizationManagerPost {}", authentication.get());
            return new AuthorizationDecision(true);
        });
    }

    /**
     * This bean is for emitting AuthorizationDeniedEvent
     *
     * @param applicationEventPublisher
     * @return
     * @see org.springframework.security.authorization.event.AuthorizationDeniedEvent
     */
    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher
    (ApplicationEventPublisher applicationEventPublisher) {
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }

    //required to expose actuator/auditevents endpoint
    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
