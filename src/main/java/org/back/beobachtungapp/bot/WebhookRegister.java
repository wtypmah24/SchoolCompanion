package org.back.beobachtungapp.bot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "TgBotWebHook is immutable and safe to inject")
@Slf4j
@Component
public class WebhookRegister {
  private final TgBot bot;
  private final String webhookUrl;
  private final String path;

  public WebhookRegister(TgBot bot, TelegramProperties telegramProperties) {
    this.bot = bot;
    this.webhookUrl = telegramProperties.getWebhook_url();
    this.path = telegramProperties.getPath();
  }

  @PostConstruct
  public void registerWebhook() {
    SetWebhook setWebhook = SetWebhook.builder().url(webhookUrl + path).build();

    try {
      bot.setWebhook(setWebhook);
      log.info("Webhook registered successfully");
    } catch (TelegramApiException e) {
      log.error("Failed to register webhook", e);
    }
  }
}
