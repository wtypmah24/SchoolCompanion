package org.back.beobachtungapp.dto.request.child;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a goal")
public record GoalRequestDto(
    @Schema(
            description = "Description of the goal",
            example = "Pay more attention to the learning",
            minLength = 5,
            maxLength = 255)
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
        String description) {}
