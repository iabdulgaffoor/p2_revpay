package com.revpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RevPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(RevPayApplication.class, args);
    }
}
