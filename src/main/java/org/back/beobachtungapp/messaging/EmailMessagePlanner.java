package org.back.beobachtungapp.messaging;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.brevo.BrevoEmailRequest;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailMessagePlanner {
  /** Delay before event notification, default 24 hours in milliseconds. */
  private static final long DELAY_IN_MS = 24 * 60 * 60 * 1000;

  /** Minimum default delay (2 minutes) if event start is sooner than 24 hours. */
  private static final long DEFAULT_DELAY_MS = 120_000;

  private final MessagingQueueManager messagingQueueManager;
  private final MessageSource messageSource;

  public void scheduleEmailOnNewEvent(EventNotificationDto event, long eventId) {
    if (event.companionEmail() == null || event.companionEmail().isBlank()) {
      log.warn("User {} didn't provide email", event.companionName());
      return;
    }

    String subject = "Event reminder: " + event.title();

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

    Instant notificationTime = Instant.parse(event.start()).minusSeconds(DELAY_IN_MS);
    if (notificationTime.isBefore(Instant.now())) {
      notificationTime = Instant.now().plusMillis(DEFAULT_DELAY_MS);
      log.info(
          "Sending email scheduled in less than {} ms. Sending email notifications now.",
          DELAY_IN_MS);
    }
    String scheduledAt = notificationTime.toString();

    String batchId = String.valueOf(eventId);

    BrevoEmailRequest emailRequest =
        new BrevoEmailRequest(
            new BrevoEmailRequest.Sender("Kinder Compass", "wtypmah48@gmail.com"),
            List.of(
                new BrevoEmailRequest.To(event.companionEmail(), event.companionName()),
                new BrevoEmailRequest.To(event.childEmail(), event.childName())),
            subject,
            messageText,
            scheduledAt,
            batchId);

    log.info("Scheduling email: {}", emailRequest);
    messagingQueueManager.scheduleEventEmail(emailRequest);
  }

  public void cancelEmailOnNewEvent(long eventId) {
    messagingQueueManager.cancelScheduledEventEmail(String.valueOf(eventId));
  }
}
