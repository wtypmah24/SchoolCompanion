package org.back.beobachtungapp.dto.response.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoalResponseDto(
    @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
        String description) {}
