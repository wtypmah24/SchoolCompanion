package org.back.beobachtungapp.dto.response.event;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Response payload for an event")
public record EventResponseDto(
    @Schema(description = "Title of the event", example = "Annual Charity Gala") String title,
    @Schema(
            description = "Detailed description of the event",
            example = "A charity event to support local shelters.")
        String description,
    @Schema(description = "Date when the event will take place", example = "2025-07-15")
        LocalDate eventDate) {}
