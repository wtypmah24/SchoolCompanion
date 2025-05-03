package org.back.beobachtungapp.dto.request.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for adding a monitoring entry")
public record MonitoringEntryRequestDto(
    @Schema(description = "Value of the monitoring entry", example = "120/80")
        @NotBlank(message = "Value cannot be blank")
        String value,
    @Schema(
            description = "Optional notes or comments related to the entry",
            example = "Measured after medication",
            nullable = true)
        // nullable field
        String notes) {}
