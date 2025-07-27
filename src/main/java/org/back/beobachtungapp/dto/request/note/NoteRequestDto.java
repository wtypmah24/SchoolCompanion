package org.back.beobachtungapp.dto.request.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequestDto(
    @NotBlank(message = "Value can't be blank")
        @Size(min = 5, max = 1000, message = "Content must be between 5 and 1000 characters")
        String content) {}
