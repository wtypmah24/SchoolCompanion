package org.back.beobachtungapp.dto.request.event;

import java.time.LocalDateTime;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.event.Event;

public record EventNotificationDto(
    String title,
    String description,
    String start,
    String end,
    String location,
    String companionName,
    String tgId,
    String childName) {
  public static EventNotificationDto from(Event event) {
    String companionName =
        Optional.ofNullable(event)
            .map(Event::getChild)
            .map(Child::getSchoolCompanion)
            .map(Companion::getName)
            .orElse("N/A");
    String tgId =
        Optional.ofNullable(event)
            .map(Event::getChild)
            .map(Child::getSchoolCompanion)
            .map(Companion::getTgId)
            .orElse(null);
    String childName =
        Optional.ofNullable(event).map(Event::getChild).map(Child::getName).orElse("Unknown child");

    assert event != null;
    return new EventNotificationDto(
        Optional.ofNullable(event.getTitle()).orElse("No title"),
        Optional.ofNullable(event.getDescription()).orElse("No description"),
        Optional.ofNullable(event.getStartDateTime())
            .map(LocalDateTime::toString)
            .orElse("No start date"),
        Optional.ofNullable(event.getEndDateTime())
            .map(LocalDateTime::toString)
            .orElse("No end date"),
        Optional.ofNullable(event.getLocation()).orElse("Не указано"),
        companionName,
        tgId,
        childName);
  }
}
