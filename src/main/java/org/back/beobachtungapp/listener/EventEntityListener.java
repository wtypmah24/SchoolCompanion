package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostPersist;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.message.DelayedMessage;
import org.back.beobachtungapp.service.DelayedMessageService;
import org.back.beobachtungapp.utils.TgUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@SuppressFBWarnings
@Component
@RequiredArgsConstructor
public class EventEntityListener {
  private final DelayedMessageService delayedMessageService;
  private final MessageSource messageSource;

  @PostPersist
  public void onPostPersist(Event event) {
    String title = event.getTitle();
    String description = event.getDescription();
    LocalDateTime start = event.getStartDateTime();
    LocalDateTime end = event.getEndDateTime();
    String location = event.getLocation();

    Child child = event.getChild();
    Companion companion = child.getSchoolCompanion();
    String companionName = companion != null ? companion.getName() : "N/A";

    String childName = child.getName();

    String messageText =
        messageSource.getMessage(
            "telegram.event.message",
            new Object[] {
              title,
              childName,
              companionName,
              start.toString(),
              end.toString(),
              location,
              description
            },
            Locale.getDefault());

    String escapedMsg = TgUtils.escapeMarkdown(messageText);
    DelayedMessage message = new DelayedMessage("341326165", escapedMsg);
    long delayMillis = 10_000;
    delayedMessageService.addDelayedMessage(message, delayMillis);
  }
}
