package org.back.beobachtungapp.dto.response.event;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Response payload for an event")
public record EventResponseDto(
    @Schema(description = "Id of the event", example = "1L") Long id,
    @Schema(description = "Title of the event", example = "Monthly meeting") String title,
    @Schema(
            description = "Detailed description of the event",
            example = "A class event to discuss children problems.")
        String description,
    @Schema(
            description = "Start date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        Instant startDateTime,
    @Schema(
            description = "End date and time of the event (must be today or in the future)",
            example = "2025-07-15")
        Instant endDateTime,
    @Schema(description = "Location of the event", example = "Classroom.") String location,
    @Schema(description = "Id of the child associated with the event", example = "1")
        Long childId) {}
