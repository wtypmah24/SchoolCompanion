package org.back.beobachtungapp.dto.response.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

@Schema(description = "Response payload for a monitoring parameter")
public record MonitoringParamResponseDto(
    Long id,
    @Schema(description = "Title of the monitoring parameter", example = "Blood Pressure")
        String title,
    @Schema(description = "Scale type used for the monitoring parameter", example = "NUMERIC")
        ScaleType type,
    @Schema(
            description = "Description of the monitoring parameter",
            example = "Measures systolic and diastolic pressure")
        String description,
    @Schema(description = "Min value of the param", example = "1") int minValue,
    @Schema(description = "Max value of the param", example = "100") int maxValue,
    Instant createdAt) {}
