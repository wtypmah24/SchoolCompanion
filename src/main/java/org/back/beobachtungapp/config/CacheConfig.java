package org.back.beobachtungapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {
  private final ObjectMapper objectMapper;

  public CacheConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper.copy();
  }

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer(objectMapper)));
  }

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory factory, RedisCacheConfiguration redisCacheConfiguration) {

    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("child", redisCacheConfiguration);
    cacheConfigurations.put("children", redisCacheConfiguration);
    cacheConfigurations.put("goals", redisCacheConfiguration);
    cacheConfigurations.put("goal", redisCacheConfiguration);
    cacheConfigurations.put("need", redisCacheConfiguration);
    cacheConfigurations.put("needs", redisCacheConfiguration);
    return RedisCacheManager.builder(factory)
        .cacheDefaults(redisCacheConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
