package org.back.beobachtungapp.dto.response.monitoring;

public record MonitoringEntryResponseDto(
    Long id,
    String value,
    String notes,
    Long parameterId,
    String parameterName,
    String type,
    Long childId,
    String createdAt) {}
