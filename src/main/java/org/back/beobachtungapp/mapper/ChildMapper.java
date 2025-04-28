package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChildMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "interests", ignore = true)
  @Mapping(target = "schoolCompanion", ignore = true)
  @Mapping(target = "specialNeeds", ignore = true)
  @Mapping(target = "goals", ignore = true)
  @Mapping(target = "monitoringParameters", ignore = true)
  @Mapping(target = "notes", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Child childRequestDtoToChild(ChildRequestDto childRequestDto);

  ChildResponseDto childToChildResponseDto(Child child);
}
