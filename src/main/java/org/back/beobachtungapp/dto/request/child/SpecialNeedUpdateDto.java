package org.back.beobachtungapp.dto.request.child;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a special need")
public record SpecialNeedUpdateDto(
    @Schema(
            description = "Type of the special need",
            example = "Mobility support",
            minLength = 2,
            maxLength = 100)
        @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters")
        String type,
    @Schema(
            description = "Detailed description of the special need",
            example = "Requires wheelchair access and ramp availability",
            minLength = 5,
            maxLength = 500)
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description) {}
