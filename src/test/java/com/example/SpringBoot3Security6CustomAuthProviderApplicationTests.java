package com.example;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
class SpringBoot3Security6CustomAuthProviderApplicationTests {

    @Test
    @WithUserDetails
    void contextLoads() {
    }

}

