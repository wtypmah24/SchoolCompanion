package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.entity.note.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "child", ignore = true)
  @Mapping(target = "companion", ignore = true)
  Note noteRequestDtoToNote(NoteRequestDto noteRequestDto);
}
