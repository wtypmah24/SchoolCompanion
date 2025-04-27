package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChildMapper {
    Child childRequestDtoToChild(ChildRequestDto childRequestDto);

    ChildResponseDto childToChildResponseDto(Child child);
}
