package org.back.beobachtungapp.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BrevoClientConfig {
  @Value("${brevo.api-key}")
  private String apiKey;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("api-key", apiKey);
      requestTemplate.header("Content-Type", "application/json");
    };
  }
}
