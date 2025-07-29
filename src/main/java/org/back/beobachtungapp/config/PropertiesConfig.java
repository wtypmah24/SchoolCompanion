package org.back.beobachtungapp.config;

import org.back.beobachtungapp.config.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
  JwtProperties.class,
  OpenAiProperties.class,
  TelegramProperties.class,
  BrevoProperties.class,
  DbProperties.class,
  AvatarProperties.class
})
public class PropertiesConfig {}
