package org.back.beobachtungapp.dto.update.companion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record CompanionUpdateDto(
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name,
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        String surname,
    @Size(min = 2, max = 50, message = "Organization must be between 2 and 50 characters")
        String organization,
    @NotBlank(message = "Email cannot be blank") String email,
    LocalTime startWorkingTime,
    LocalTime endWorkingTime) {}
