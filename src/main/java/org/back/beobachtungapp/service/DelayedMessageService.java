package org.back.beobachtungapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.message.DelayedMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@SuppressFBWarnings
@Slf4j
@Service
@RequiredArgsConstructor
public class DelayedMessageService {
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  public void addDelayedMessage(DelayedMessage msg, long delayMillis) {
    long executionTime = Instant.now().toEpochMilli() + delayMillis;

    try {
      String json = objectMapper.writeValueAsString(msg);
      redisTemplate.opsForZSet().add("delayedMessages", json, executionTime);
      log.info("Message added to Redis with delay: {} at {}", msg, executionTime);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize delayed message: {}", msg, e);
    }
  }

  public void addEventDelayedMessage(DelayedMessage msg, long delayMillis) {}
}
