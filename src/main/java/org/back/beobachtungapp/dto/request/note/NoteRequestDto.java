package org.back.beobachtungapp.dto.request.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteRequestDto(
    @NotBlank(message = "Value can't be blank") String content,
    @NotNull(message = "Child id is required") Long childId,
    @NotNull(message = "Companion ID is required") Long companionId) {}
