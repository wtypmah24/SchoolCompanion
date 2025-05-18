package org.back.beobachtungapp.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.bot.TgBot;
import org.back.beobachtungapp.dto.message.DelayedTgMessage;
import org.back.beobachtungapp.dto.telegram.TelegramPdfJob;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

  @Scheduled(fixedRate = 60000)
  public void processDelayedMessages() {
    processPdfJobs();
    processTgDelayedMessages();
  }

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
