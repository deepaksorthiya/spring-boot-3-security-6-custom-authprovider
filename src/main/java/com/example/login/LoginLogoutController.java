package com.example.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginLogoutController {

    @RequestMapping("/login")
    public String loginPage() {
        return "/login/login";
    }

    @RequestMapping("/logout")
    public String logoutPage() {
        return "/logout/logout";
    }
}