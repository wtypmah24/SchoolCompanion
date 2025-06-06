package org.back.beobachtungapp.dto.request.event;

import java.time.Instant;
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
    String companionEmail,
    String childEmail,
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
    String companionEmail =
        Optional.ofNullable(event)
            .map(Event::getChild)
            .map(Child::getSchoolCompanion)
            .map(Companion::getEmail)
            .orElse(null);
    String childEmail =
        Optional.ofNullable(event).map(Event::getChild).map(Child::getEmail).orElse(null);
    String childName =
        Optional.ofNullable(event).map(Event::getChild).map(Child::getName).orElse("Unknown child");

    assert event != null;
    return new EventNotificationDto(
        Optional.ofNullable(event.getTitle()).orElse("No title"),
        Optional.ofNullable(event.getDescription()).orElse("No description"),
        Optional.ofNullable(event.getStartDateTime())
            .map(Instant::toString)
            .orElse("No start date"),
        Optional.ofNullable(event.getEndDateTime()).map(Instant::toString).orElse("No end date"),
        Optional.ofNullable(event.getLocation()).orElse("Не указано"),
        companionName,
        tgId,
        companionEmail,
        childEmail,
        childName);
  }
}
