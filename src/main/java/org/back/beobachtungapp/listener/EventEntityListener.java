package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.dto.message.DelayedTgMessage;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.service.MessageQueueService;
import org.back.beobachtungapp.utils.TgUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@SuppressFBWarnings
@Component
@RequiredArgsConstructor
public class EventEntityListener {
  private final MessageQueueService messageQueueService;
  private final MessageSource messageSource;
  private static final long DELAY_IN_MS = 24 * 60 * 60 * 1000;
  private static final long DEFAULT_DELAY_MS = 120_000;

  @PostPersist
  public void onPostPersist(Event event) {
    try {
      planTgMessage(event);
    } catch (Exception e) {
      log.warn("Failed to plan TG message for event {}: {}", event.getId(), e.getMessage(), e);
    }

    try {
      scheduleEmail(event);
    } catch (Exception e) {
      log.warn("Failed to schedule email for event {}: {}", event.getId(), e.getMessage(), e);
    }
  }

  @PostUpdate
  public void onPostUpdate(Event event) {
    String id = String.valueOf(event.getId());

    try {
      messageQueueService.cancelScheduledTelegramMessage(id);
    } catch (Exception e) {
      log.warn("Failed to remove TG message for event {}: {}", id, e.getMessage(), e);
    }

    try {
      planTgMessage(event);
    } catch (Exception e) {
      log.warn("Failed to re-plan TG message for event {}: {}", id, e.getMessage(), e);
    }

    try {
      messageQueueService.cancelScheduledEmail(id);
    } catch (Exception e) {
      log.warn("Failed to cancel email for event {}: {}", id, e.getMessage(), e);
    }

    try {
      scheduleEmail(event);
    } catch (Exception e) {
      log.warn("Failed to reschedule email for event {}: {}", id, e.getMessage(), e);
    }
  }

  @PostRemove
  public void onPostRemove(Event event) {
    try {
      messageQueueService.cancelScheduledTelegramMessage(String.valueOf(event.getId()));
    } catch (Exception e) {
      log.warn(
          "Failed to remove delayed TG message for event {}: {}", event.getId(), e.getMessage(), e);
    }

    try {
      messageQueueService.cancelScheduledEmail(String.valueOf(event.getId()));
    } catch (Exception e) {
      log.warn(
          "Failed to cancel scheduled email for event {}: {}", event.getId(), e.getMessage(), e);
    }
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
    messageQueueService.scheduleTelegramMessage(message, delayMillis);
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

    Instant notificationTime = event.getStartDateTime().minusSeconds(DELAY_IN_MS);
    if (notificationTime.isBefore(Instant.now())) {
      notificationTime = Instant.now().plusMillis(DEFAULT_DELAY_MS);
      log.info(
          "Sending email scheduled in less than {} ms. Sending email notifications now.",
          DELAY_IN_MS);
    }
    String scheduledAt = notificationTime.toString();

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
    messageQueueService.scheduleEmail(emailRequest);
  }

  private Long calculateDelay(Event event) {
    Instant start = event.getStartDateTime();
    Instant now = Instant.now();

    // TODO: change to meaningfully delay
    Instant notificationTime = start.minusSeconds(DELAY_IN_MS);

    long delayMillis = java.time.Duration.between(now, notificationTime).toMillis();

    // TODO: remove magic number
    return Math.max(delayMillis, DEFAULT_DELAY_MS);
  }
}
