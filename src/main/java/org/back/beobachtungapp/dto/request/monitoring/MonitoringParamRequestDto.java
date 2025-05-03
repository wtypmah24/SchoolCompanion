package org.back.beobachtungapp.dto.request.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

@Schema(description = "Request payload for creating a new monitoring parameter")
public record MonitoringParamRequestDto(
    @Schema(
            description = "Title of the monitoring parameter",
            example = "Blood Pressure",
            minLength = 3,
            maxLength = 100)
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
    @Schema(description = "Type of scale used for monitoring", example = "NUMERIC")
        @NotNull(message = "Scale type is required")
        ScaleType scaleType,
    @Schema(
            description = "Description of the monitoring parameter",
            example = "Measures systolic and diastolic pressure",
            minLength = 5,
            maxLength = 500)
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description) {}
