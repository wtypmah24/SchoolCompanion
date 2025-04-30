package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpecialNeedMapper {

  SpecialNeed specialNeedRequestDtoToSpecialNeed(SpecialNeedRequestDto specialNeedRequestDto);
}
