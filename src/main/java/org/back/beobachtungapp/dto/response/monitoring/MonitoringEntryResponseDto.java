package org.back.beobachtungapp.dto.response.monitoring;

import java.time.Instant;

public record MonitoringEntryResponseDto(
    Long id,
    String value,
    String notes,
    Long parameterId,
    String parameterName,
    String type,
    Long childId,
    Instant createdAt) {}
