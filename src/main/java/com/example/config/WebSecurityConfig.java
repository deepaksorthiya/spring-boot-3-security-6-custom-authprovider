package com.example.config;

import com.example.security.CustomAuthenticationProvider;
import com.example.security.CustomJdbcUserDetailManager;
import com.example.security.CustomWebAuthenticationDetailsSource;
import org.modelmapper.ModelMapper;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationManagerAfterMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity(/*debug = true*/)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager, CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource) throws Exception {
        http
                .formLogin(formLogin -> formLogin.authenticationDetailsSource(customWebAuthenticationDetailsSource)
                        .loginPage("/login").permitAll().defaultSuccessUrl("/home", true))
                .logout(logout -> logout.logoutUrl("/logout").permitAll())
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        //allow h2 console, all actuator endpoints and all static content
                        .requestMatchers(PathRequest.toH2Console(), PathRequest.toStaticResources().atCommonLocations(), EndpointRequest.toAnyEndpoint()).permitAll()
                        //custom authorization manager
                        .requestMatchers("/user/**", "/admin/**", "/principal/**", "/authUser/**", "/change-password/**", "/home/**").access(customAuthorizationManager)
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

//2nd way to implement custom AuthenticationManager
//    @Autowired
//    public void configure(AuthenticationManagerBuilder builder, MessageSource messageSource, UserDetailsService userDetailsService) {
//        //configure AuthenticationManagerBuilder here
//        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
//        authenticationProvider.setMessageSource(messageSource);
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        builder.authenticationProvider(authenticationProvider);
//        System.out.println(builder);
//    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder, MessageSource messageSource, CustomJdbcUserDetailManager userDetailsService) {
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setMessageSource(messageSource);

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

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor preAuthorize(MyAuthorizationManagerPre myAuthorizationManagerPre) {
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(myAuthorizationManagerPre);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    Advisor postAuthorize(MyAuthorizationManagerPost myAuthorizationManagerPost) {
        return AuthorizationManagerAfterMethodInterceptor.postAuthorize(myAuthorizationManagerPost);
    }

    //required to expose actuator/auditevents endpoint
    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
