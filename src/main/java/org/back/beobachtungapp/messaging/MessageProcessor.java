package org.back.beobachtungapp.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.message.DelayedTgMessage;
import org.back.beobachtungapp.dto.telegram.TelegramPdfJob;
import org.back.beobachtungapp.integration.telegram.TgBot;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Service responsible for processing delayed messages including PDF jobs from Redis queues and
 * sending them through the Telegram bot.
 *
 * <p>This service periodically polls Redis for scheduled Telegram messages and PDF sending jobs,
 * then dispatches them accordingly.
 */
@SuppressFBWarnings
@Slf4j
@Service
public class MessageProcessor {

  private static final String DELAYED_MESSAGES_KEY = "delayedMessages";
  private static final String TELEGRAM_PDF_QUEUE_KEY = "telegram:pdf:queue";

  private final RedisTemplate<String, String> redisTemplate;
  private final RedisTemplate<String, Object> redisPdfTemplate;
  private final TgBot tgBot;
  private final ObjectMapper objectMapper;

  public MessageProcessor(
      RedisTemplate<String, String> redisTemplate,
      RedisTemplate<String, Object> redisPdfTemplate,
      TgBot tgBot,
      ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.redisPdfTemplate = redisPdfTemplate;
    this.tgBot = tgBot;
    this.objectMapper = objectMapper;
  }

  /**
   * Scheduled method that runs every 60 seconds to process pending jobs.
   *
   * <p>It triggers processing of:
   *
   * <ul>
   *   <li>PDF sending jobs from the PDF Redis queue
   *   <li>Delayed Telegram text messages from the Redis sorted set
   * </ul>
   */
  @Scheduled(fixedRate = 60000)
  public void processDelayedMessages() {
    processPdfJobs();
    processTgDelayedMessages();
  }

  /**
   * Processes a single PDF job from the Redis list queue.
   *
   * <p>Pops a job from the right of the queue, converts it to {@link TelegramPdfJob}, then uses
   * {@link TgBot} to send the PDF to the user. If no jobs are found, simply logs and returns.
   */
  private void processPdfJobs() {
    Object obj = redisPdfTemplate.opsForList().rightPop(TELEGRAM_PDF_QUEUE_KEY);

    if (obj == null) {
      log.debug("No PDF jobs found in queue.");
      return;
    }

    try {
      TelegramPdfJob job = objectMapper.convertValue(obj, TelegramPdfJob.class);
      if (job != null) {
        tgBot.sendPdfToUser(job.telegramId(), job.pdfData(), job.fileName());
        log.info("Sent PDF to user: telegramId={}", job.telegramId());
      }
    } catch (Exception e) {
      log.error("Failed to process PDF job: {}", obj, e);
    }
  }

  /**
   * Processes all delayed Telegram text messages from Redis sorted set whose scheduled time (score)
   * is less than or equal to the current time.
   *
   * <p>For each message:
   *
   * <ul>
   *   <li>Deserializes it to {@link DelayedTgMessage}
   *   <li>Constructs a {@link SendMessage} with MarkdownV2 parse mode
   *   <li>Sends the message through {@link TgBot}
   *   <li>Removes the message from the Redis sorted set
   * </ul>
   *
   * <p>If processing fails for any message, it is removed to avoid retries.
   */
  private void processTgDelayedMessages() {
    long currentTime = System.currentTimeMillis();

    Set<String> messages =
        redisTemplate.opsForZSet().rangeByScore(DELAYED_MESSAGES_KEY, 0, currentTime);

    if (messages == null || messages.isEmpty()) {
      log.debug("No delayed Telegram messages to process at {}", currentTime);
      return;
    }

    for (String messageJson : messages) {
      try {
        DelayedTgMessage msg = objectMapper.readValue(messageJson, DelayedTgMessage.class);

        SendMessage telegramMsg = new SendMessage();
        telegramMsg.setParseMode(ParseMode.MARKDOWNV2);
        telegramMsg.setChatId(msg.chatId());
        telegramMsg.setText(msg.message());

        tgBot.execute(telegramMsg);

        redisTemplate.opsForZSet().remove(DELAYED_MESSAGES_KEY, messageJson);

        log.info("Processed and removed message for chatId {}: {}", msg.chatId(), messageJson);
      } catch (Exception e) {
        log.error("Failed to process delayed message: {}", messageJson, e);
        redisTemplate.opsForZSet().remove(DELAYED_MESSAGES_KEY, messageJson);
      }
    }
  }
}
