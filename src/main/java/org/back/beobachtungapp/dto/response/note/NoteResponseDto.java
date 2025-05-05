package org.back.beobachtungapp.dto.response.note;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload for a note")
public record NoteResponseDto(
    @Schema(description = "Id of the note", example = "1L") Long id,
    @Schema(description = "Content of the note", example = "This is a note about the event.")
        String content) {}
