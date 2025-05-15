package org.back.beobachtungapp.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class OpenAiFeignConfig {
  @Value("${openai.api.key}")
  private String apiKey;

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("Authorization", "Bearer " + apiKey);
      requestTemplate.header("OpenAI-Beta", "assistants=v2");
      requestTemplate.header("Content-Type", "application/json");
    };
  }
}
