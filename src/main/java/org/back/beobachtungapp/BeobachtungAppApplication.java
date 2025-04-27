package org.back.beobachtungapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BeobachtungAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeobachtungAppApplication.class, args);
    }

}
