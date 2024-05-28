package com.example.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class LoginLogoutController {

    @RequestMapping("/login")
    public String loginPage(Principal principal) {
        if (principal != null) {
            return "redirect:/home";
        }
        return "/login/login";
    }

    @RequestMapping("/logout")
    public String logoutPage() {
        return "/logout/logout";
    }
}