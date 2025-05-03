package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.request.child.ChildUpdateDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {SpecialNeedMapper.class, GoalMapper.class, NoteMapper.class})
public interface ChildMapper {

  Child childRequestDtoToChild(ChildRequestDto childRequestDto);

  @Mapping(target = "specialNeeds", source = "specialNeeds")
  ChildResponseDto childToChildResponseDto(Child child);

  List<ChildResponseDto> childToChildResponseDtoList(List<Child> children);

  void updateChildFromDto(ChildUpdateDto dto, @MappingTarget Child entity);
}
