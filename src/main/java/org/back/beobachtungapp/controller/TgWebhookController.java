package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.back.beobachtungapp.integration.telegram.TgBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "TgBotWebHook is immutable and safe to inject")
@RestController
public class TgWebhookController {
  private final TgBot bot;

  public TgWebhookController(TgBot bot) {
    this.bot = bot;
  }

  @PostMapping("/webhook/callback/webhook")
  public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
    return bot.onWebhookUpdateReceived(update);
  }
}
