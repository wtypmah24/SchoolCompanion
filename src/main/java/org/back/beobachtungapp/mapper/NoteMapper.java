package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.back.beobachtungapp.entity.note.Note;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoteMapper {
  Note noteRequestDtoToNote(NoteRequestDto noteRequestDto);

  NoteResponseDto noteToNoteResponseDto(Note note);

  List<NoteResponseDto> noteToNotesResponseDtoList(List<Note> notes);

  void updateNote(NoteRequestDto dto, @MappingTarget Note note);
}
