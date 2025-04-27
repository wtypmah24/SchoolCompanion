package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanionMapper {
    Companion companionRequestDtoToCompanion(CompanionRequestDto companionRequestDto);
}
