package org.back.beobachtungapp.messaging;

import static org.back.beobachtungapp.utils.MessageUtil.calculateDelay;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.message.TelegramMessage;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
import org.back.beobachtungapp.dto.response.session.SessionMessageDto;
import org.back.beobachtungapp.utils.TgUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TgMessagePlanner {
  private final MessagingQueueManager messagingQueueManager;
  private final MessageSource messageSource;

  public void planTgMessageOnNewEvent(EventNotificationDto event, long eventId) {
    if (event.tgId() == null) {
      log.warn("User {} didn't provide telegram id", event.companionName());
      return;
    }
    String messageText =
        messageSource.getMessage(
            "telegram.event.message",
            new Object[] {
              event.title(),
              event.childName(),
              event.companionName(),
              event.start(),
              event.end(),
              event.location(),
              event.description()
            },
            Locale.getDefault());

    String escapedMsg = TgUtils.escapeMarkdown(messageText);
    TelegramMessage message = new TelegramMessage(event.tgId(), escapedMsg, eventId);
    log.info("Telegram message: {}", message);
    long delayMillis = calculateDelay(event);
    log.info("Delayed Tg Message for {} ms", delayMillis);
    messagingQueueManager.scheduleTelegramMessage(message, "event:", delayMillis);
  }

  public void handleSessionMessage(SessionMessageDto session) {
    try {
      if (session != null
          && !session.tgId().isEmpty()
          && (session.workSessionResponseDto().endTime() == null)) {
        return;
      }

      assert session != null;
      Duration duration =
          Duration.between(
              session.workSessionResponseDto().startTime(),
              session.workSessionResponseDto().endTime());
      long hours = duration.toHours();
      long minutes = duration.minusHours(hours).toMinutes();
      Instant sessionDate = session.workSessionResponseDto().startTime();
      String message =
          String.format(
              "✅ Your work session for %s has ended.\n🕒 Duration: %d hours %d minutes.",
              sessionDate, hours, minutes);

      String escapedMsg = TgUtils.escapeMarkdown(message);
      TelegramMessage telegramMessage =
          new TelegramMessage(session.tgId(), escapedMsg, session.workSessionResponseDto().id());

      messagingQueueManager.scheduleTelegramMessage(telegramMessage, "session:", 1000);

    } catch (Exception e) {
      log.error("Failed to send tg session msg to: {}", session.tgId(), e);
    }
  }

  public void cancelTgMessageOnNewEvent(long eventId) {
    messagingQueueManager.cancelScheduledEventTelegramMessage(String.valueOf(eventId));
  }
}
