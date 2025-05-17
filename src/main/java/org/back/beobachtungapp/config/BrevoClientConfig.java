package org.back.beobachtungapp.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.config.properties.BrevoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BrevoClientConfig {
  private final BrevoProperties properties;

  @Bean
  public RequestInterceptor requestBrevoInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("api-key", properties.getApi_key());
      requestTemplate.header("Content-Type", "application/json");
    };
  }
}
