package org.back.beobachtungapp.dto.request.monitoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.back.beobachtungapp.entity.monitoring.ScaleType;

public record MonitoringParameterRequestDto(

        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        @NotNull(message = "Scale type is required")
        ScaleType scaleType,

        @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description

        // monitoringParameterId can be sent throw URL, for example:
        // POST /monitoring/{parameterId}/entries
) {
}
