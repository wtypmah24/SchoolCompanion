package org.back.beobachtungapp.config;

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

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
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
    cacheConfigurations.put("params", redisCacheConfiguration);
    cacheConfigurations.put("param", redisCacheConfiguration);
    cacheConfigurations.put("events", redisCacheConfiguration);
    cacheConfigurations.put("notes", redisCacheConfiguration);
    cacheConfigurations.put("note", redisCacheConfiguration);
    cacheConfigurations.put("entry", redisCacheConfiguration);
    cacheConfigurations.put("entries", redisCacheConfiguration);
    cacheConfigurations.put("task", redisCacheConfiguration);
    cacheConfigurations.put("tasks", redisCacheConfiguration);
    return RedisCacheManager.builder(factory)
        .cacheDefaults(redisCacheConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
