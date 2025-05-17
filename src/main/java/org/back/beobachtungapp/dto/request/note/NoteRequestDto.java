package org.back.beobachtungapp.dto.request.note;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new note")
public record NoteRequestDto(
    @Schema(description = "Content of the note", example = "This is a note about the event.")
        @NotBlank(message = "Value can't be blank")
        @Size(min = 5, max = 1000, message = "Content must be between 5 and 1000 characters")
        String content) {}
