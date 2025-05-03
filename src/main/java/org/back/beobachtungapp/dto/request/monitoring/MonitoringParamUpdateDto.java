package org.back.beobachtungapp.dto.request.monitoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

@Schema(
    description =
        "Request payload for updating a monitoring parameter. All fields are optional, but if provided, they must be valid.")
public record MonitoringParamUpdateDto(
    @Schema(
            description = "Updated title of the monitoring parameter",
            example = "Updated Blood Pressure",
            minLength = 3,
            maxLength = 100)
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
    @Schema(description = "Updated scale type of the monitoring parameter", example = "NUMERIC")
        ScaleType scaleType,
    @Schema(
            description = "Updated description of the monitoring parameter",
            example = "Updated description for measuring systolic and diastolic pressure",
            minLength = 5,
            maxLength = 500)
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description) {}
