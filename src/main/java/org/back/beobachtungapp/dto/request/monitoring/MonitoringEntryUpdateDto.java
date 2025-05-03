package org.back.beobachtungapp.dto.request.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for updating a monitoring entry. All fields are optional.")
public record MonitoringEntryUpdateDto(
    @Schema(
            description = "Updated value of the monitoring entry",
            example = "130/85",
            nullable = true)
        String value,
    @Schema(
            description = "Updated notes or comments",
            example = "After light exercise",
            nullable = true)
        String notes) {}
