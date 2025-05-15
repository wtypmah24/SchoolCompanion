package org.back.beobachtungapp.dto.request.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Schema(description = "Request payload for creating a new event")
public record EventRequestDto(
    @Schema(description = "Title of the event", example = "Monthly meeting")
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,
    @Schema(
            description = "Detailed description of the event",
            example = "A class event to discuss children problems.")
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description,
    @Schema(
            description = "Start date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        @NotNull(message = "Event date is required")
        @FutureOrPresent(message = "Event date must be today or in the future")
        Instant startDateTime,
    @Schema(
            description = "End date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        @NotNull(message = "Event date is required")
        @FutureOrPresent(message = "Event date must be today or in the future")
        Instant endDateTime,
    @Schema(description = "Location of the event", example = "Classroom.")
        @NotBlank(message = "Location cannot be blank")
        @Size(min = 2, max = 500, message = "Location must be between 2 and 500 characters")
        String location) {}
