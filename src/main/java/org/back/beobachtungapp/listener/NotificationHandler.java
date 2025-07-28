package org.back.beobachtungapp.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.event.EventNotificationDto;
import org.back.beobachtungapp.dto.response.session.SessionMessageDto;
import org.back.beobachtungapp.messaging.EmailMessagePlanner;
import org.back.beobachtungapp.messaging.TgMessagePlanner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler {

  private final ObjectMapper objectMapper;
  private final TgMessagePlanner tgMessagePlanner;
  private final EmailMessagePlanner emailMessagePlanner;

  public void handleNotification(String payload) {
    try {
      JsonNode root = objectMapper.readTree(payload);
      String type = root.path("type").asText();
      long id = root.path("id").asLong();

      switch (type) {
        case "event_created" -> handleCreated(payload, id);
        case "event_updated" -> handleUpdated(payload, id);
        case "event_deleted" -> handleDeleted(id);
        case "session_updated" -> handleSessionUpdated(payload, id);
        default -> log.warn("Unknown event type: {}", type);
      }

    } catch (Exception e) {
      log.error("Failed to parse event notification payload: {}", payload, e);
    }
  }

  private void handleCreated(String payload, long eventId) {
    EventNotificationDto event = null;
    try {
      event = objectMapper.readValue(payload, EventNotificationDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    try {
      tgMessagePlanner.planTgMessageOnNewEvent(event, eventId);
    } catch (Exception e) {
      log.warn("Failed to plan TG message for event {}: {}", eventId, e.getMessage(), e);
    }

    try {
      emailMessagePlanner.scheduleEmailOnNewEvent(event, eventId);
    } catch (Exception e) {
      log.warn("Failed to schedule email for event {}: {}", eventId, e.getMessage(), e);
    }
  }

  private void handleDeleted(Long eventId) {
    try {
      tgMessagePlanner.cancelTgMessageOnNewEvent(eventId);
    } catch (Exception e) {
      log.warn("Failed to remove delayed TG message for event {}: {}", eventId, e.getMessage(), e);
    }

    try {
      emailMessagePlanner.cancelEmailOnNewEvent(eventId);
    } catch (Exception e) {
      log.warn("Failed to cancel scheduled email for event {}: {}", eventId, e.getMessage(), e);
    }
  }

  private void handleUpdated(String payload, long eventId) {
    EventNotificationDto event = null;
    try {
      event = objectMapper.readValue(payload, EventNotificationDto.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    try {
      tgMessagePlanner.cancelTgMessageOnNewEvent(eventId);
    } catch (Exception e) {
      log.warn("Failed to remove TG message for event {}: {}", eventId, e.getMessage(), e);
    }

    try {
      tgMessagePlanner.planTgMessageOnNewEvent(event, eventId);
    } catch (Exception e) {
      log.warn("Failed to re-plan TG message for event {}: {}", eventId, e.getMessage(), e);
    }

    try {
      emailMessagePlanner.cancelEmailOnNewEvent(eventId);
    } catch (Exception e) {
      log.warn("Failed to cancel email for event {}: {}", eventId, e.getMessage(), e);
    }

    try {
      emailMessagePlanner.scheduleEmailOnNewEvent(event, eventId);
    } catch (Exception e) {
      log.warn("Failed to reschedule email for event {}: {}", eventId, e.getMessage(), e);
    }
  }

  private void handleSessionUpdated(String payload, long id) {
    SessionMessageDto messageDto = null;
    try {
      messageDto = objectMapper.readValue(payload, SessionMessageDto.class);
      if (messageDto.tgId() == null || messageDto.tgId().isBlank()) return;
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    tgMessagePlanner.handleSessionMessage(messageDto);
  }
}
