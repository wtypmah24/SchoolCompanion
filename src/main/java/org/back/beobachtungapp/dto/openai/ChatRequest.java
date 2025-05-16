package org.back.beobachtungapp.dto.openai;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "Prompt cannot be blank") String message) {}
