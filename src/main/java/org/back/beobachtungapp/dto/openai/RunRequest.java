package org.back.beobachtungapp.dto.openai;

import jakarta.validation.constraints.NotBlank;

public record RunRequest(@NotBlank(message = "Assistant ID can't be blank") String assistant_id) {}
