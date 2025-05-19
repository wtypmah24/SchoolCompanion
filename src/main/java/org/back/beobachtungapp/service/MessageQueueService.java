package org.back.beobachtungapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.dto.message.DelayedTgMessage;
import org.back.beobachtungapp.dto.telegram.TelegramPdfJob;
import org.back.beobachtungapp.feign.BrevoClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing messaging queues, including scheduling delayed Telegram
 * messages, enqueuing PDF sending jobs, and handling email scheduling and cancellation via the
 * Brevo email client.
 *
 * <p>Uses Redis to store delayed messages and message queues.
 */
@SuppressFBWarnings
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueueService {

  private static final String DELAYED_MESSAGES_KEY = "delayedMessages";
  private static final String EVENT_KEY_PREFIX = "event:";
  private static final String TELEGRAM_PDF_QUEUE_KEY = "telegram:pdf:queue";

  private final RedisTemplate<String, String> redisTemplate;
  private final RedisTemplate<String, Object> redisPdfTemplate;
  private final ObjectMapper objectMapper;
  private final BrevoClient brevoClient;

  /**
   * Schedules a delayed Telegram message to be sent on adding new event after a specified delay.
   * The message is serialized and stored in a Redis sorted set with a score representing the
   * scheduled execution time.
   *
   * @param msg the delayed Telegram message to schedule
   * @param delayMillis the delay in milliseconds before sending the message
   */
  public void scheduleEventTelegramMessage(DelayedTgMessage msg, long delayMillis) {
    if (msg == null) {
      log.warn("Attempted to schedule null Telegram message");
      return;
    }

    long executionTime = Instant.now().toEpochMilli() + delayMillis;

    try {
      String json = serializeMessage(msg);
      redisTemplate.opsForZSet().add(DELAYED_MESSAGES_KEY, json, executionTime);
      redisTemplate.opsForValue().set(EVENT_KEY_PREFIX + msg.eventId(), json);

      log.info(
          "Scheduled Telegram message with delay={} ms at time={} for eventId={}",
          delayMillis,
          executionTime,
          msg.eventId());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize delayed Telegram message: {}", msg, e);
    }
  }

  /**
   * Cancels a previously scheduled Telegram message identified by its event ID. Removes the message
   * from Redis sorted set and deletes the associated key.
   *
   * @param eventId the unique event ID of the scheduled Telegram message to cancel
   */
  public void cancelScheduledEventTelegramMessage(String eventId) {
    if (eventId == null || eventId.isEmpty()) {
      log.warn("Attempted to cancel Telegram message with null or empty eventId");
      return;
    }

    String redisKey = EVENT_KEY_PREFIX + eventId;
    String json = redisTemplate.opsForValue().get(redisKey);

    if (json != null) {
      redisTemplate.opsForZSet().remove(DELAYED_MESSAGES_KEY, json);
      redisTemplate.delete(redisKey);
      log.info("Cancelled scheduled Telegram message with eventId={}", eventId);
    } else {
      log.warn("No scheduled Telegram message found in Redis for eventId={}", eventId);
    }
  }

  /**
   * Enqueues a Telegram PDF job into a Redis list queue for asynchronous processing. Ensures the
   * job and Telegram ID are not null before enqueuing.
   *
   * @param job the TelegramPdfJob containing PDF data and recipient Telegram ID
   */
  public void enqueueTelegramPdfReportJob(TelegramPdfJob job) {
    if (job == null || job.telegramId() == null) {
      log.warn("TelegramPdfJob or telegramId is null - skipping enqueue");
      return;
    }

    redisPdfTemplate.opsForList().leftPush(TELEGRAM_PDF_QUEUE_KEY, job);
    log.info("Enqueued Telegram PDF job for telegramId={}", job.telegramId());
  }

  /**
   * Schedules an email to be sent on adding new event via the Brevo email client. Logs a warning if
   * the request is null.
   *
   * @param request the BrevoEmailRequest containing email details to send
   */
  public void scheduleEventEmail(BrevoEmailRequest request) {
    if (request == null) {
      log.warn("Attempted to schedule null email request");
      return;
    }
    brevoClient.sendEmail(request);
    log.info("Scheduled email via BrevoClient: {}", request);
  }

  /**
   * Attempts to cancel a scheduled email identified by a batch ID or message ID via the Brevo email
   * client. Logs the result or any errors encountered.
   *
   * @param batchIdOrMessageId the batch ID or message ID of the scheduled email to cancel
   */
  public void cancelScheduledEventEmail(String batchIdOrMessageId) {
    if (batchIdOrMessageId == null || batchIdOrMessageId.isEmpty()) {
      log.warn("Attempted to cancel email with null or empty batch/message ID");
      return;
    }

    try {
      brevoClient.cancelEmail(batchIdOrMessageId);
      log.info("Cancelled scheduled email with ID={}", batchIdOrMessageId);
    } catch (Exception e) {
      log.error("Failed to cancel scheduled email with ID: {}", batchIdOrMessageId, e);
    }
  }

  /**
   * Serializes a DelayedTgMessage to a JSON string using the configured ObjectMapper.
   *
   * @param msg the delayed Telegram message to serialize
   * @return the JSON string representation of the message
   * @throws JsonProcessingException if serialization fails
   */
  private String serializeMessage(DelayedTgMessage msg) throws JsonProcessingException {
    return objectMapper.writeValueAsString(msg);
  }
}
