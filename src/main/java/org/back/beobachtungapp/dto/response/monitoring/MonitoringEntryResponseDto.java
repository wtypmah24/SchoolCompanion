package org.back.beobachtungapp.dto.response.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response payload for a monitoring entry")
public record MonitoringEntryResponseDto(
    Long id,
    @Schema(description = "Value of the monitoring entry", example = "120/80") String value,
    @Schema(
            description = "Additional notes or comments related to the monitoring entry",
            example = "Measured after exercise")
        String notes,
    @Schema(description = "ID of the associated monitoring parameter", example = "10")
        Long parameterId,
    @Schema(
            description = "Name of the associated monitoring parameter",
            example = "Emotional state")
        String parameterName,
    @Schema(description = "Type of the associated monitoring parameter", example = "BINARY")
        String type,
    @Schema(description = "ID of the associated child", example = "5") Long childId,
    LocalDateTime createdAt) {}
