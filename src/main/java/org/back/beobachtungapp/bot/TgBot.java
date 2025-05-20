package org.back.beobachtungapp.bot;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.config.properties.TelegramProperties;
import org.back.beobachtungapp.dto.request.companion.CompanionAdTgIdDto;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  public TgBot(TelegramProperties telegramProperties, CompanionService companionService) {
    super(new DefaultBotOptions(), telegramProperties.getToken());
    this.username = telegramProperties.getUsername();
    this.path = telegramProperties.getPath();
    this.enabled = telegramProperties.isEnabled();
    this.companionService = companionService;
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    if (!enabled || !update.hasMessage() || !update.getMessage().hasText()) {
      return null;
    }

    String text = update.getMessage().getText().trim();
    Long chatId = update.getMessage().getChatId();

    return userStates.containsKey(chatId)
        ? handleStatefulMessage(chatId, text)
        : handleCommand(chatId, text);
  }

  private SendMessage handleCommand(Long chatId, String text) {
    return switch (text.toLowerCase()) {
      case "/connect_tgid" -> {
        userStates.put(chatId, BotState.AWAITING_EMAIL);
        yield createMessage(chatId, "Enter your email in KinderCompass app");
      }
      default -> createMessage(chatId, "Unknown command. Use /connect_tgid to link your Telegram.");
    };
  }

  private SendMessage handleStatefulMessage(Long chatId, String text) {
    BotState state = userStates.get(chatId);
    if (state == BotState.AWAITING_EMAIL) {
      return processEmail(chatId, text);
    }
    return createMessage(chatId, "Unexpected state. Please try again.");
  }

  private SendMessage processEmail(Long chatId, String email) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId.toString());

    try {
      companionService.addTgIdToCompanion(new CompanionAdTgIdDto(chatId.toString(), email));
      message.setText("✅ Telegram successfully linked to your account.");
      log.info("Telegram ID linked: chatId={}, email={}", chatId, email);
    } catch (Exception e) {
      log.error(
          "Failed to link Telegram ID for chat {} with email {}: {}",
          chatId,
          email,
          e.getMessage(),
          e);
      message.setText("❌ Failed to link Telegram. Please check your email and try again.");
    } finally {
      userStates.remove(chatId);
    }

    return message;
  }

  private SendMessage createMessage(Long chatId, String text) {
    SendMessage msg = new SendMessage();
    msg.setChatId(chatId.toString());
    msg.setText(text);
    return msg;
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
