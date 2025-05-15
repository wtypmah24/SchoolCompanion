package org.back.beobachtungapp.dto.request.companion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanionAdTgIdDto(
    @NotBlank(message = "Telegram Id cannot be blank") String tgId,
    @Email(message = "Email should be valid") @NotBlank(message = "Email cannot be blank")
        String email) {}
