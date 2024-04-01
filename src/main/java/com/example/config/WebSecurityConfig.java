package com.example.config;

import com.example.security.CustomAuthenticationProvider;
import com.example.security.CustomJdbcUserDetailManager;
import com.example.security.CustomWebAuthenticationDetailsSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/**
 * @author Deepak Katariya
 * @apiNote Spring security config
 */
@Configuration
@EnableWebSecurity(/*debug = true*/)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    private final DataSource dataSource;

    private final CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource;

    public WebSecurityConfig(DataSource dataSource, CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource) {
        this.dataSource = dataSource;
        this.customWebAuthenticationDetailsSource = customWebAuthenticationDetailsSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.authenticationDetailsSource(customWebAuthenticationDetailsSource)
                        .loginPage("/login").permitAll().defaultSuccessUrl("/home", true))
                .logout(logout -> logout.logoutUrl("/logout").permitAll().logoutSuccessUrl("/login"))
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(toH2Console()).permitAll().anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .anonymous(anonymousConfigurer -> anonymousConfigurer.disable())
        ;
        return http.build();
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder builder, MessageSource messageSource) {
        //configure AuthenticationManagerBuilder here
        CustomJdbcUserDetailManager userDetailsService = new CustomJdbcUserDetailManager(dataSource);
        userDetailsService.setRolePrefix("ROLE_");
        userDetailsService.setMessageSource(messageSource);
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
        authenticationProvider.setMessageSource(messageSource);
        authenticationProvider.setUserDetailsService(userDetailsService);
        builder.authenticationProvider(authenticationProvider);
        System.out.println(builder);
    }

//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    public AuthenticationManager authenticationManager(
//            PasswordEncoder passwordEncoder, MessageSource messageSource) {
//        CustomJdbcUserDetailManager userDetailsService = new CustomJdbcUserDetailManager(dataSource);
//        userDetailsService.setMessageSource(messageSource);
//        userDetailsService.setRolePrefix("ROLE_");
//        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder);
//        authenticationProvider.setMessageSource(messageSource);
//        ProviderManager providerManager = new ProviderManager(authenticationProvider);
//        providerManager.setMessageSource(messageSource);
//        providerManager.setAuthenticationEventPublisher(new DefaultAuthenticationEventPublisher());
//        userDetailsService.setAuthenticationManager(providerManager);
//        return providerManager;
//    }


//    @Bean(BeanIds.USER_DETAILS_SERVICE)
//    public CustomJdbcUserDetailManager userDetailsServiceBean(MessageSource messageSource) {
//        CustomJdbcUserDetailManager userDetailsServiceBean = new CustomJdbcUserDetailManager(dataSource);
//        userDetailsServiceBean.setMessageSource(messageSource);
//        userDetailsServiceBean.setRolePrefix("ROLE_");
//        return userDetailsServiceBean;
//    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    //required to expose actuator/auditevents endpoint
    @Bean
    public AuditEventRepository auditEventRepository() {
        return new InMemoryAuditEventRepository();
    }
}
