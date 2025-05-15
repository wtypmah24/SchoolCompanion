package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostPersist;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.message.DelayedTgMessage;
import org.back.beobachtungapp.service.DelayedMessageService;
import org.back.beobachtungapp.utils.TgUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@SuppressFBWarnings
@Component
@RequiredArgsConstructor
public class EventEntityListener {
  private final DelayedMessageService delayedMessageService;
  private final MessageSource messageSource;

  @PostPersist
  public void onPostPersist(Event event) {
    planTgMessage(event);
  }

  private void planTgMessage(Event event) {
    EventNotificationDto dto = EventNotificationDto.from(event);
    if (dto.tgId() == null) {
      log.warn("User {} didn't provide telegram id", dto.companionName());
      return;
    }
    String messageText =
        messageSource.getMessage(
            "telegram.event.message",
            new Object[] {
              dto.title(),
              dto.childName(),
              dto.companionName(),
              dto.start(),
              dto.end(),
              dto.location(),
              dto.description()
            },
            Locale.getDefault());

    String escapedMsg = TgUtils.escapeMarkdown(messageText);
    DelayedTgMessage message = new DelayedTgMessage(dto.tgId(), escapedMsg);
    log.info("Telegram message: {} ms", message);
    long delayMillis = calculateDelay(event);
    log.info("Delayed Tg Message for {} ms", delayMillis);
    delayedMessageService.addDelayedMessage(message, delayMillis);
  }

  private void planSendEmail(Event event) {}

  private Long calculateDelay(Event event) {
    LocalDateTime start = event.getStartDateTime();
    LocalDateTime now = LocalDateTime.now();

    LocalDateTime notificationTime = start.minusDays(1);

    long delayMillis = java.time.Duration.between(now, notificationTime).toMillis();

    return Math.max(delayMillis, 0);
  }
}
