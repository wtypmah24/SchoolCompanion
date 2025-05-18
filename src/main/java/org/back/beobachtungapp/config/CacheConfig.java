package org.back.beobachtungapp.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

  @Bean(name = "redisTemplate")
  public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }

  @Bean(name = "redisPdfTemplate")
  public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(Object.class);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(serializer);
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(serializer);

    template.afterPropertiesSet();

    return template;
  }
}
