package org.back.beobachtungapp.dto.response.monitoring;

import java.time.Instant;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

public record MonitoringParamResponseDto(
    Long id,
    String title,
    ScaleType type,
    String description,
    int minValue,
    int maxValue,
    Instant createdAt) {}
