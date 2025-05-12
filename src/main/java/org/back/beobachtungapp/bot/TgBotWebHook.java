package org.back.beobachtungapp.bot;

import org.back.beobachtungapp.config.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TgBotWebHook extends TelegramWebhookBot {

  private final String username;
  private final String path;
  private final boolean enabled;

  public TgBotWebHook(TelegramProperties telegramProperties) {
    super(new DefaultBotOptions(), telegramProperties.getToken());
    this.username = telegramProperties.getUsername();
    this.path = telegramProperties.getPath();
    this.enabled = telegramProperties.isEnabled();
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText() && enabled) {
      Long chatId = update.getMessage().getChatId();
      SendMessage message = new SendMessage();
      message.setChatId(chatId.toString());
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
