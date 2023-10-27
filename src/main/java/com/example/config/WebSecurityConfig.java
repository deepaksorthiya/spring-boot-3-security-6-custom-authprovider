package com.example.config;

import com.example.security.CustomAuthenticationProvider;
import com.example.security.CustomJdbcUserDetailManager;
import com.example.security.CustomWebAuthenticationDetailsSource;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
@EnableWebSecurity
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
                .formLogin(formLogin -> formLogin.authenticationDetailsSource(customWebAuthenticationDetailsSource).loginPage("/login").permitAll().defaultSuccessUrl("/home", true))
                .logout(logout -> logout.logoutUrl("/logout").permitAll().logoutSuccessUrl("/login"))
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(toH2Console()).permitAll().anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authenticationProvider(customAuthenticationProvider())
        ;
        return http.build();
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config, CustomJdbcUserDetailManager userDetailsService)
            throws Exception {
        AuthenticationManager authenticationManager = config.getAuthenticationManager();
        userDetailsService.setAuthenticationManager(authenticationManager);
        return authenticationManager;
    }

    public CustomAuthenticationProvider customAuthenticationProvider() {
        CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceBean());
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        return authenticationProvider;
    }

    @Bean(BeanIds.USER_DETAILS_SERVICE)
    public CustomJdbcUserDetailManager userDetailsServiceBean() {
        CustomJdbcUserDetailManager userDetailsServiceBean = new CustomJdbcUserDetailManager(dataSource);
        userDetailsServiceBean.setRolePrefix("ROLE_");
        return userDetailsServiceBean;
    }

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
