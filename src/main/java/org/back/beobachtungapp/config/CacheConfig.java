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
    GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer));
  }

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory factory, RedisCacheConfiguration redisCacheConfiguration) {

    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("child", redisCacheConfiguration);
    cacheConfigurations.put("goal", redisCacheConfiguration);
    cacheConfigurations.put("need", redisCacheConfiguration);
    cacheConfigurations.put("param", redisCacheConfiguration);
    cacheConfigurations.put("note", redisCacheConfiguration);
    cacheConfigurations.put("entry", redisCacheConfiguration);
    cacheConfigurations.put("task", redisCacheConfiguration);
    return RedisCacheManager.builder(factory)
        .cacheDefaults(redisCacheConfiguration)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
