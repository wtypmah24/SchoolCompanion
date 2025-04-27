package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.entity.note.Note;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    Note noteRequestDtoToNote(NoteRequestDto noteRequestDto);
}
