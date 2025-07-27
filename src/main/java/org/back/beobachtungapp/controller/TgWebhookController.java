package org.back.beobachtungapp.controller;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.integration.telegram.TgBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class TgWebhookController {
  private final TgBot bot;

  @PostMapping("/webhook/callback/webhook")
  public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
    return bot.onWebhookUpdateReceived(update);
  }
}
