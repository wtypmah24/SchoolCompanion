package org.back.beobachtungapp.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Configuration
@Validated
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
  @NotBlank private String username;
  @NotBlank private String token;
  @NotBlank private String path;
  @NotBlank private String webhook_url;
  private boolean enabled;
}
