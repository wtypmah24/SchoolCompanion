package org.back.beobachtungapp.dto.update.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Schema(
    description =
        "Request payload for updating an existing event. All fields are optional but must be valid if provided.")
public record EventUpdateDto(
    @Schema(description = "Title of the event", example = "Monthly meeting")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
    @Schema(
            description = "Detailed description of the event",
            example = "A class event to discuss children problems.")
        @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
        String description,
    @Schema(
            description = "Start date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        @FutureOrPresent(message = "Event date must be today or in the future")
        Instant startDateTime,
    @Schema(
            description = "End date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        @FutureOrPresent(message = "Event date must be today or in the future")
        Instant endDateTime,
    @Schema(description = "Location of the event", example = "Classroom.")
        @NotBlank(message = "Location cannot be blank")
        @Size(min = 2, max = 500, message = "Location must be between 2 and 500 characters")
        String location) {}
