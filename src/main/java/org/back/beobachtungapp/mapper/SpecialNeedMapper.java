package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpecialNeedMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "child", ignore = true)
  SpecialNeed specialNeedRequestDtoToSpecialNeed(SpecialNeedRequestDto specialNeedRequestDto);
}
