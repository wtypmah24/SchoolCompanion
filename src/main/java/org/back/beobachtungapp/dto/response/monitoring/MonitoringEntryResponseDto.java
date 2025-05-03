package org.back.beobachtungapp.dto.response.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload for a monitoring entry")
public record MonitoringEntryResponseDto(
    @Schema(description = "Value of the monitoring entry", example = "120/80") String value,
    @Schema(
            description = "Additional notes or comments related to the monitoring entry",
            example = "Measured after exercise")
        String notes) {}
