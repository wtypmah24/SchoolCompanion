package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpecialNeedMapper {
    SpecialNeed specialNeedRequestDtoToSpecialNeed(SpecialNeedRequestDto specialNeedRequestDto);
}
