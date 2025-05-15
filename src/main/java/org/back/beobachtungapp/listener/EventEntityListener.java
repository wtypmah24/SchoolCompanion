package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
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
    scheduleEmail(event);
  }

  @PostUpdate
  public void onPostUpdate(Event event) {
    delayedMessageService.removeDelayedTgMessage(String.valueOf(event.getId()));
    planTgMessage(event);
    delayedMessageService.cancelScheduledEmail(String.valueOf(event.getId()));
    scheduleEmail(event);
  }

  @PostRemove
  public void onPostRemove(Event event) {
    delayedMessageService.removeDelayedTgMessage(String.valueOf(event.getId()));
    delayedMessageService.cancelScheduledEmail(String.valueOf(event.getId()));
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
    DelayedTgMessage message = new DelayedTgMessage(dto.tgId(), escapedMsg, event.getId());
    log.info("Telegram message: {}", message);
    long delayMillis = calculateDelay(event);
    log.info("Delayed Tg Message for {} ms", delayMillis);
    delayedMessageService.addDelayedTgMessage(message, delayMillis);
  }

  private void scheduleEmail(Event event) {
    EventNotificationDto dto = EventNotificationDto.from(event);
    if (dto.companionEmail() == null || dto.companionEmail().isBlank()) {
      log.warn("User {} didn't provide email", dto.companionName());
      return;
    }

    String subject = "Event reminder: " + dto.title();

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

    LocalDateTime notificationTime = event.getStartDateTime().minusDays(1);
    String scheduledAt =
        notificationTime
            .atZone(ZoneId.systemDefault())
            .withZoneSameInstant(ZoneOffset.ofHours(2))
            .toString();

    String batchId = String.valueOf(event.getId());

    BrevoEmailRequest emailRequest =
        new BrevoEmailRequest(
            new BrevoEmailRequest.Sender("Kinder Compass", "wtypmah48@gmail.com"),
            List.of(
                new BrevoEmailRequest.To(dto.companionEmail(), dto.companionName()),
                new BrevoEmailRequest.To(dto.childEmail(), dto.childName())),
            subject,
            messageText,
            scheduledAt,
            batchId);

    log.info("Scheduling email: {}", emailRequest);
    delayedMessageService.scheduleEmail(emailRequest);
  }

  private Long calculateDelay(Event event) {
    LocalDateTime start = event.getStartDateTime();
    LocalDateTime now = LocalDateTime.now();

    // TODO: change to meaningfully delay
    LocalDateTime notificationTime = start.minusDays(1);

    long delayMillis = java.time.Duration.between(now, notificationTime).toMillis();

    long DEFAULT_DELAY_MS = 30_000;
    return Math.max(delayMillis, DEFAULT_DELAY_MS);
  }
}
