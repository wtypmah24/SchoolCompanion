package org.back.beobachtungapp.dto.request.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(
    description =
        "Request payload for updating an existing event. All fields are optional but must be valid if provided.")
public record EventUpdateDto(
    @Schema(
            description = "Updated title of the event",
            example = "Updated Gala Title",
            minLength = 3,
            maxLength = 100)
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
    @Schema(
            description = "Updated event description",
            example = "Updated description for the annual charity gala",
            minLength = 5,
            maxLength = 500)
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description,
    @Schema(
            description = "Updated date of the event (must be today or in the future)",
            example = "2025-08-01")
        @FutureOrPresent(message = "Event date must be today or in the future")
        LocalDate eventDate) {}
