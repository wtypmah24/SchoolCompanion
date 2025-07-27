package org.back.beobachtungapp.dto.update.event;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record EventUpdateDto(
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters") String title,
    @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
        String description,
    @FutureOrPresent(message = "Event date must be today or in the future") Instant startDateTime,
    @FutureOrPresent(message = "Event date must be today or in the future") Instant endDateTime,
    @NotBlank(message = "Location cannot be blank")
        @Size(min = 2, max = 500, message = "Location must be between 2 and 500 characters")
        String location) {}
