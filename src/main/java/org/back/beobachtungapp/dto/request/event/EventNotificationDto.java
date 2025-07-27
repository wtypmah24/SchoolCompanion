package org.back.beobachtungapp.dto.request.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    String childName) {}
