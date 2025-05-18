package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.child.ChildWithAttachments;
import org.back.beobachtungapp.dto.update.child.ChildUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {
      SpecialNeedMapper.class,
      GoalMapper.class,
      NoteMapper.class,
      EventMapper.class,
      MonitoringEntryMapper.class
    })
public interface ChildMapper {

  Child childRequestDtoToChild(ChildRequestDto childRequestDto);

  ChildResponseDto childToChildResponseDto(Child child);

  List<ChildResponseDto> childToChildResponseDtoList(List<Child> children);

  @Mapping(target = "specialNeeds", source = "specialNeeds")
  @Mapping(target = "goals", source = "goals")
  @Mapping(target = "notes", source = "notes")
  @Mapping(target = "entries", source = "entries")
  @Mapping(target = "events", source = "events")
  ChildWithAttachments childToChildWithAttachments(Child child);

  void updateChildFromDto(ChildUpdateDto dto, @MappingTarget Child entity);
}
