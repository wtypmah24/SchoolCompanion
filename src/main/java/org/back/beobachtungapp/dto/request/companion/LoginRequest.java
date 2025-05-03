package org.back.beobachtungapp.dto.request.companion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user login containing email and password")
public record LoginRequest(
    @Schema(description = "User email address", example = "user@example.com")
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email cannot be blank")
        String email,
    @Schema(description = "User password", example = "StrongPassword123!")
        @NotBlank(message = "Password cannot be blank")
        String password) {}
