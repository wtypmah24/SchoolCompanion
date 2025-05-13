package org.back.beobachtungapp.listener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.PostPersist;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
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
    EventNotificationDto dto = EventNotificationDto.from(event);
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
    DelayedMessage message = new DelayedMessage("341326165", escapedMsg);
    long delayMillis = 10_000;
    delayedMessageService.addDelayedMessage(message, delayMillis);
  }
}
