package org.back.beobachtungapp.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.config.properties.OpenAiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAiFeignConfig {

  private final OpenAiProperties openAiProperties;

  @Bean
  public RequestInterceptor requestOpenAiInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("Authorization", "Bearer " + openAiProperties.getKey());
      requestTemplate.header("OpenAI-Beta", "assistants=v2");
      requestTemplate.header("Content-Type", "application/json");
    };
  }
}
