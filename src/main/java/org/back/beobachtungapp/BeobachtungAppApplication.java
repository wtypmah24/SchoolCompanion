package org.back.beobachtungapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class BeobachtungAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(BeobachtungAppApplication.class, args);
  }
}
