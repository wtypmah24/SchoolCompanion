package org.back.beobachtungapp.utils;

import java.time.Instant;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;

public class MessageUtil {
  /** Delay before event notification, default 24 hours in milliseconds. */
  private static final long DELAY_IN_MS = 24 * 60 * 60 * 1000;

  /** Minimum default delay (2 minutes) if event start is sooner than 24 hours. */
  private static final long DEFAULT_DELAY_MS = 120_000;

  public static Long calculateDelay(EventNotificationDto event) {
    Instant start = Instant.parse(event.start());
    Instant now = Instant.now();

    // TODO: change to meaningfully delay
    Instant notificationTime = start.minusSeconds(DELAY_IN_MS);

    long delayMillis = java.time.Duration.between(now, notificationTime).toMillis();

    // TODO: remove magic number
    return Math.max(delayMillis, DEFAULT_DELAY_MS);
  }
}
