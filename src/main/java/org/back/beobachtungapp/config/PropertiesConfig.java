package org.back.beobachtungapp.config;

import org.back.beobachtungapp.config.properties.BrevoProperties;
import org.back.beobachtungapp.config.properties.JwtProperties;
import org.back.beobachtungapp.config.properties.OpenAiProperties;
import org.back.beobachtungapp.config.properties.TelegramProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  JwtProperties.class,
  OpenAiProperties.class,
  TelegramProperties.class,
  BrevoProperties.class
})
public class PropertiesConfig {}
