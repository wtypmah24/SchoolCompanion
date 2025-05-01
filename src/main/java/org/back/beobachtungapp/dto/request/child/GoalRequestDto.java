package org.back.beobachtungapp.dto.request.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoalRequestDto(
    @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
        String description

    // We don't include entityId here, if we send child id in URL:
    // POST /children/{id}/goals
    ) {}
