package org.back.beobachtungapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.dto.message.DelayedTgMessage;
import org.back.beobachtungapp.feign.BrevoClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@SuppressFBWarnings
@Slf4j
@Service
@RequiredArgsConstructor
public class DelayedMessageService {
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final BrevoClient brevoClient;

  public void addDelayedTgMessage(DelayedTgMessage msg, long delayMillis) {
    long executionTime = Instant.now().toEpochMilli() + delayMillis;

    try {
      String json = objectMapper.writeValueAsString(msg);
      redisTemplate.opsForZSet().add("delayedMessages", json, executionTime);

      redisTemplate.opsForValue().set("event:" + msg.eventId(), json);
      log.info("Message added to Redis with delay: {} at {}", msg, executionTime);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize delayed message: {}", msg, e);
    }
  }

  public void removeDelayedTgMessage(String eventId) {
    String redisKey = "event:" + eventId;
    String json = redisTemplate.opsForValue().get(redisKey);

    if (json != null) {
      redisTemplate.opsForZSet().remove("delayedMessages", json);
      redisTemplate.delete(redisKey);
      log.info("Message removed from Redis: {}", eventId);
    } else {
      log.warn("No message found in Redis for eventId: {}", eventId);
    }
  }

  public void scheduleEmail(BrevoEmailRequest request) {
    brevoClient.sendEmail(request);
  }

  public void cancelScheduledEmail(String batchIdOrMessageId) {
    try {
      brevoClient.cancelEmail(batchIdOrMessageId);
    } catch (Exception e) {
      log.error("Failed to cancel scheduled email with ID: {}", batchIdOrMessageId, e);
    }
  }
}
