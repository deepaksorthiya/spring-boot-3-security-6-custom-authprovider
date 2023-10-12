package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBoot3Security6CustomAuthProviderApplication implements ApplicationRunner {

    final
    PasswordEncoder passwordEncoder;

    public SpringBoot3Security6CustomAuthProviderApplication(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot3Security6CustomAuthProviderApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {

        System.out.println("User : "+ passwordEncoder.encode("password"));
        System.out.println("Admin : "+ passwordEncoder.encode("admin"));


    }
}
