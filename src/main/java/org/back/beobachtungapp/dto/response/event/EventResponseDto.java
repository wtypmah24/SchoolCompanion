package org.back.beobachtungapp.dto.response.event;

import java.time.Instant;

public record EventResponseDto(
    Long id,
    String title,
    String description,
    Instant startDateTime,
    Instant endDateTime,
    String location,
    Long childId) {}
