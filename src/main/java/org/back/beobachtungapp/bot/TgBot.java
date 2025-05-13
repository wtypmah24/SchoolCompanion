package org.back.beobachtungapp.bot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.back.beobachtungapp.config.TelegramProperties;
import org.back.beobachtungapp.message.DelayedMessage;
import org.back.beobachtungapp.service.DelayedMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@SuppressFBWarnings
@Component
public class TgBot extends TelegramWebhookBot {

  private final String username;
  private final String path;
  private final boolean enabled;
  private final DelayedMessageService delayedMessageService;

  public TgBot(TelegramProperties telegramProperties, DelayedMessageService delayedMessageService) {
    super(new DefaultBotOptions(), telegramProperties.getToken());
    this.username = telegramProperties.getUsername();
    this.path = telegramProperties.getPath();
    this.enabled = telegramProperties.isEnabled();
    this.delayedMessageService = delayedMessageService;
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText() && enabled) {
      Long chatId = update.getMessage().getChatId();
      SendMessage message = new SendMessage();
      message.setChatId(chatId.toString());
      DelayedMessage msg = new DelayedMessage(chatId.toString(), "Pong after 1 min");
      delayedMessageService.addDelayedMessage(msg, 6000);
      message.setText("Pong");

      return message;
    }

    return null;
  }

  @Override
  public String getBotPath() {
    return path;
  }

  @Override
  public String getBotUsername() {
    return username;
  }
}
