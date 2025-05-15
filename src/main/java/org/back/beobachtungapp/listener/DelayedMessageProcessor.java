package org.back.beobachtungapp.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.bot.TgBot;
import org.back.beobachtungapp.message.DelayedTgMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@SuppressFBWarnings
@Slf4j
@Service
public class DelayedMessageProcessor {
  private final RedisTemplate<String, String> redisTemplate;
  private final TgBot tgBot;
  private final ObjectMapper objectMapper;

  public DelayedMessageProcessor(
      RedisTemplate<String, String> redisTemplate, TgBot tgBot, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.tgBot = tgBot;
    this.objectMapper = objectMapper;
  }

  @Scheduled(fixedRate = 60000)
  public void processDelayedMessages() {
    long currentTime = System.currentTimeMillis();

    Set<String> messages =
        redisTemplate.opsForZSet().rangeByScore("delayedMessages", 0, currentTime);

    if (messages != null && !messages.isEmpty()) {
      for (String messageJson : messages) {
        try {
          DelayedTgMessage msg = objectMapper.readValue(messageJson, DelayedTgMessage.class);

          SendMessage telegramMsg = new SendMessage();
          telegramMsg.setParseMode(ParseMode.MARKDOWNV2);
          telegramMsg.setChatId(msg.chatId());
          telegramMsg.setText(msg.message());

          tgBot.execute(telegramMsg);

          redisTemplate.opsForZSet().remove("delayedMessages", messageJson);
          log.info("Processed and removed message: {}", messageJson);
        } catch (Exception e) {
          log.error("Failed to process message: {}", messageJson, e);
          redisTemplate.opsForZSet().remove("delayedMessages", messageJson);
        }
      }
    }
  }
}
