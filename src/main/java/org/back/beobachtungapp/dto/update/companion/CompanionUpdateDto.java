package org.back.beobachtungapp.dto.update.companion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a companion user")
public record CompanionUpdateDto(
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        @Schema(description = "Name of the companion", example = "John")
        String name,
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        @Schema(description = "Surname of the companion", example = "Doe")
        String surname,
    @Size(min = 2, max = 50, message = "Organization must be between 2 and 50 characters")
        @Schema(description = "Organization of the companion", example = "Good Company GmbH")
        String organization,
    @NotBlank(message = "Email cannot be blank")
        @Schema(description = "Email address of the companion", example = "john.doe@example.com")
        String email) {}
