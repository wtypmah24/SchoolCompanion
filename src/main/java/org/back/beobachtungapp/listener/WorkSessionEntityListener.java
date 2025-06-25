package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.message.TelegramMessage;
import org.back.beobachtungapp.entity.session.WorkSession;
import org.back.beobachtungapp.messaging.MessagingQueueManager;
import org.back.beobachtungapp.utils.TgUtils;
import org.springframework.stereotype.Component;

@Slf4j
@SuppressFBWarnings
@Component
@RequiredArgsConstructor
public class WorkSessionEntityListener {
  private final MessagingQueueManager queueManager;

  private static final ThreadLocal<WorkSession> oldWorkSessionState = new ThreadLocal<>();

  @PreUpdate
  public void onPreUpdate(WorkSession workSession) {
    WorkSession snapshot = WorkSession.copyOf(workSession);
    oldWorkSessionState.set(snapshot);
  }

  @PostUpdate
  public void onPostUpdate(WorkSession workSession) {
    WorkSession oldState = oldWorkSessionState.get();
    if (oldState != null
        && !workSession.getCompanion().getTgId().isEmpty()
        && (oldState.getEndTime() == null && workSession.getEndTime() != null)) {
      return;
    }
    Duration duration = Duration.between(workSession.getStartTime(), workSession.getEndTime());
    long hours = duration.toHours();
    long minutes = duration.minusHours(hours).toMinutes();
    Instant sessionDate = workSession.getStartTime();
    String message =
        String.format(
            "✅ Your work session for %s has ended.\n🕒 Duration: %d hours %d minutes.",
            sessionDate, hours, minutes);
    String tgId = workSession.getCompanion().getTgId();
    if (tgId.isEmpty()) {
      return;
    }

    String escapedMsg = TgUtils.escapeMarkdown(message);
    TelegramMessage telegramMessage = new TelegramMessage(tgId, escapedMsg, workSession.getId());

    queueManager.scheduleTelegramMessage(telegramMessage, "session:", 1000);
    oldWorkSessionState.remove();
  }
}
