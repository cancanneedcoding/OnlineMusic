package com.example.onlimemusic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class OnlimemusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlimemusicApplication.class, args);
    }

}
