package org.back.beobachtungapp.dto.openai;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
    @NotBlank(message = "Role cannot be blank") String role,
    @NotBlank(message = "Content cannot be blank") String content) {}
