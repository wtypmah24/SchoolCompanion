package org.back.beobachtungapp.bot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.TelegramProperties;
import org.back.beobachtungapp.dto.request.companion.CompanionAdTgIdDto;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@SuppressFBWarnings
@Component
public class TgBot extends TelegramWebhookBot {
  private final Map<Long, BotState> userStates = new ConcurrentHashMap<>();

  private enum BotState {
    AWAITING_EMAIL
  }

  private final String username;
  private final String path;
  private final boolean enabled;
  private final CompanionService companionService;

  public TgBot(TelegramProperties telegramProperties, CompanionService companionService) {
    super(new DefaultBotOptions(), telegramProperties.getToken());
    this.username = telegramProperties.getUsername();
    this.path = telegramProperties.getPath();
    this.enabled = telegramProperties.isEnabled();
    this.companionService = companionService;
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText() && enabled) {
      return handleTgCommands(update);
    }
    return null;
  }

  private SendMessage handleTgCommands(Update update) {
    String text = update.getMessage().getText().trim();
    Long chatId = update.getMessage().getChatId();
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());

    if (text.equalsIgnoreCase("/connect_tgid")) {
      userStates.put(chatId, BotState.AWAITING_EMAIL);
      message.setText("Enter your email in KinderCompass app");
      return message;
    }

    if (userStates.get(chatId) == BotState.AWAITING_EMAIL) {
      try {
        CompanionAdTgIdDto tgIdDto = new CompanionAdTgIdDto(chatId.toString(), text);
        companionService.addTgIdToCompanion(tgIdDto);
        message.setText("✅ Telegram successfully linked to your account.");
      } catch (Exception e) {
        log.error(
            "Failed to link Telegram ID for chat {} with email {}: {}",
            chatId,
            text,
            e.getMessage(),
            e);
        message.setText("❌ Failed to link Telegram. Please check your email and try again.");
      } finally {
        userStates.remove(chatId);
      }
      return message;
    }

    message.setText("Unknown command. Use /connect_tgid to link your Telegram.");
    return message;
  }

  @Async
  public void sendPdfToUser(String chatId, byte[] pdfBytes, String fileName) {
    try {
      SendDocument sendDocumentRequest = new SendDocument();
      sendDocumentRequest.setChatId(chatId);

      InputFile inputFile = new InputFile();
      inputFile.setMedia(new ByteArrayInputStream(pdfBytes), fileName);
      sendDocumentRequest.setDocument(inputFile);
      sendDocumentRequest.setCaption("Here's your report 📄");

      this.execute(sendDocumentRequest);
    } catch (TelegramApiException e) {
      log.error("Telegram API Exception: {}", e.getMessage());
    }
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
