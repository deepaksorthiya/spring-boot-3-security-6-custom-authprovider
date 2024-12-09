package com.example.usermgmt;

import com.example.security.CustomJdbcUserDetailManager;
import com.example.security.LocalizedThrowingMethodAuthorizationDeniedHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Map;

@RestController
@HandleAuthorizationDenied(handlerClass = LocalizedThrowingMethodAuthorizationDeniedHandler.class)
public class UserController {

    private final UserDetailsService userDetailsService;

    final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostAuthorize("hasAnyRole('USER','ADMIN')")
    public AppUser getUser() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("User profile {}", appUser);
        return appUser;
    }

    @GetMapping("/admin")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
    @PostAuthorize("hasRole('ADMIN')")
    public AppUser getAdmin() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Admin profile {}", appUser);
        return appUser;
    }

    @GetMapping("/principal")
    public Principal principal(Principal principal) {
        logger.info("Principal Object :: {} ", principal);
        return principal;
    }

    @GetMapping("/authentication")
    public Authentication authentication(Authentication authentication) {
        logger.info("Authentication Object :: {} ", authentication);
        return authentication;
    }

    @GetMapping("/authUser")
    public AppUser authUser(@AuthenticationPrincipal AppUser appUser) {
        return appUser;
    }

    @GetMapping("/change-password")
    public AppUser changePassword(@AuthenticationPrincipal AppUser appUser) {
        final CustomJdbcUserDetailManager customJdbcUserDetailManager = (CustomJdbcUserDetailManager) userDetailsService;
        customJdbcUserDetailManager.changePassword("admin", passwordEncoder.encode("admin"));
        return appUser;
    }

    @GetMapping("/exception")
    @PreAuthorize("hasAuthority('ROLE_XXXX')")
    public AppUser getRoleXXXXUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping(value = "/server-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getRequestInfo(@RequestHeader Map<String, String> httpHeaders, HttpServletRequest httpServletRequest) {
        httpHeaders.put("remoteHost", httpServletRequest.getRemoteHost());
        httpHeaders.put("localAddress", httpServletRequest.getLocalAddr());
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            httpHeaders.put("hostName", localHost.getHostName());
            httpHeaders.put("hostAddress", localHost.getHostAddress());
            httpHeaders.put("canonicalHostName", localHost.getCanonicalHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        logger.info("request headers :: {}", httpHeaders);
        return httpHeaders;
    }

}
