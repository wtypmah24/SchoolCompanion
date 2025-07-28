package org.back.beobachtungapp.dto.response.monitoring;

import org.back.beobachtungapp.entity.monitoring.ScaleType;

public record MonitoringParamResponseDto(
    Long id,
    String title,
    ScaleType type,
    String description,
    int minValue,
    int maxValue,
    String createdAt) {}
