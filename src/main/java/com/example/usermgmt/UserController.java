package com.example.usermgmt;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author deepakk
 * @date Sep 9, 2019
 */
@RestController
public class UserController {

    private final ModelMapper modelMapper;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @GetMapping("/user")
    public UserProfile getUser() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfile userProfile = modelMapper.map(appUser, UserProfile.class);
        logger.info("User profile", userProfile);
        return userProfile;
    }

    @GetMapping("/admin")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PreAuthorize("hasRole('ADMIN')")
    public UserProfile getAdmin() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserProfile userProfile = modelMapper.map(appUser, UserProfile.class);
        return userProfile;
    }

    @GetMapping("/principal")
    public Principal getPrincipal(Principal principal) {
        return principal;
    }

    @GetMapping("/authUser")
    public AppUser authUser(@AuthenticationPrincipal AppUser appUser) {
        return appUser;
    }

}
