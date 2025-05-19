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

/**
 * Listener for JPA entity lifecycle events on {@link Event} entities.
 *
 * <p>Handles scheduling and cancelling of delayed Telegram messages and email notifications when
 * {@link Event} entities are created, updated, or deleted.
 *
 * <p>Uses {@link MessageQueueService} to schedule/cancel messages and {@link MessageSource} to
 * obtain localized message templates.
 */
@Slf4j
@SuppressFBWarnings
@Component
@RequiredArgsConstructor
public class EventEntityListener {
  private final MessageQueueService messageQueueService;
  private final MessageSource messageSource;

  /** Delay before event notification, default 24 hours in milliseconds. */
  private static final long DELAY_IN_MS = 24 * 60 * 60 * 1000;

  /** Minimum default delay (2 minutes) if event start is sooner than 24 hours. */
  private static final long DEFAULT_DELAY_MS = 120_000;

  /**
   * Called after an {@link Event} entity is persisted.
   *
   * <p>Attempts to schedule Telegram message and email notification for the event.
   *
   * @param event the persisted event entity
   */
  @PostPersist
  public void onPostPersist(Event event) {
    try {
      planTgMessageOnNewEvent(event);
    } catch (Exception e) {
      log.warn("Failed to plan TG message for event {}: {}", event.getId(), e.getMessage(), e);
    }

    try {
      scheduleEmailOnNewEvent(event);
    } catch (Exception e) {
      log.warn("Failed to schedule email for event {}: {}", event.getId(), e.getMessage(), e);
    }
  }

  /**
   * Called after an {@link Event} entity is updated.
   *
   * <p>Cancels previously scheduled Telegram message and email notifications, then attempts to
   * reschedule them according to the updated event details.
   *
   * @param event the updated event entity
   */
  @PostUpdate
  public void onPostUpdate(Event event) {
    String id = String.valueOf(event.getId());

    try {
      messageQueueService.cancelScheduledEventTelegramMessage(id);
    } catch (Exception e) {
      log.warn("Failed to remove TG message for event {}: {}", id, e.getMessage(), e);
    }

    try {
      planTgMessageOnNewEvent(event);
    } catch (Exception e) {
      log.warn("Failed to re-plan TG message for event {}: {}", id, e.getMessage(), e);
    }

    try {
      messageQueueService.cancelScheduledEventEmail(id);
    } catch (Exception e) {
      log.warn("Failed to cancel email for event {}: {}", id, e.getMessage(), e);
    }

    try {
      scheduleEmailOnNewEvent(event);
    } catch (Exception e) {
      log.warn("Failed to reschedule email for event {}: {}", id, e.getMessage(), e);
    }
  }

  /**
   * Called after an {@link Event} entity is removed.
   *
   * <p>Cancels all scheduled Telegram messages and email notifications related to the event.
   *
   * @param event the removed event entity
   */
  @PostRemove
  public void onPostRemove(Event event) {
    try {
      messageQueueService.cancelScheduledEventTelegramMessage(String.valueOf(event.getId()));
    } catch (Exception e) {
      log.warn(
          "Failed to remove delayed TG message for event {}: {}", event.getId(), e.getMessage(), e);
    }

    try {
      messageQueueService.cancelScheduledEventEmail(String.valueOf(event.getId()));
    } catch (Exception e) {
      log.warn(
          "Failed to cancel scheduled email for event {}: {}", event.getId(), e.getMessage(), e);
    }
  }

  /**
   * Plans (schedules) a delayed Telegram message for the given event.
   *
   * <p>If the Telegram ID of the recipient is missing, logs a warning and aborts. Retrieves message
   * template from {@link MessageSource} and escapes markdown. Then schedules the message with
   * calculated delay.
   *
   * @param event the event to notify about
   */
  private void planTgMessageOnNewEvent(Event event) {
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
    messageQueueService.scheduleEventTelegramMessage(message, delayMillis);
  }

  /**
   * Schedules an email notification for the given event.
   *
   * <p>If recipient's email is missing or blank, logs a warning and aborts. Constructs the email
   * message and schedules it at a computed notification time.
   *
   * @param event the event to notify about
   */
  private void scheduleEmailOnNewEvent(Event event) {
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
    messageQueueService.scheduleEventEmail(emailRequest);
  }

  /**
   * Calculates the delay in milliseconds before sending a notification message, based on the event
   * start time and configured delay constants.
   *
   * @param event the event for which to calculate delay
   * @return delay in milliseconds, minimum of {@link #DEFAULT_DELAY_MS}
   */
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
