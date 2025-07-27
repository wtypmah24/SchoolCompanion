package org.back.beobachtungapp.dto.update.monitoring;

import jakarta.validation.constraints.Size;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

public record MonitoringParamUpdateDto(
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") String title,
    ScaleType scaleType,
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description) {}
