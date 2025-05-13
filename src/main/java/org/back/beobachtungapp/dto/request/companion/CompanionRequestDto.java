package org.back.beobachtungapp.dto.request.companion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for registering or creating a companion user")
public record CompanionRequestDto(
    @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        @Schema(description = "Name of the companion", example = "John")
        String name,
    @NotBlank(message = "Surname cannot be blank")
        @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
        @Schema(description = "Surname of the companion", example = "Doe")
        String surname,
    @Schema(description = "Telegram id of the companion", example = "31245667") String tgId,
    @Email(message = "Email should be valid")
        @NotBlank(message = "Email cannot be blank")
        @Schema(description = "Email address of the companion", example = "john.doe@example.com")
        String email,
    @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "Password for the companion", example = "password123")
        String password) {}
