package org.back.beobachtungapp.dto.response.child;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@SuppressFBWarnings
@Schema(description = "Response payload for a goal")
public record GoalResponseDto(
    @Schema(
            description = "Description of the goal",
            example = "Complete the marathon by the end of the year",
            minLength = 5,
            maxLength = 255)
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 255, message = "Description must be between 5 and 255 characters")
        String description) {}
