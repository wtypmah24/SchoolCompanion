package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanionMapper {

  Companion companionRequestDtoToCompanion(CompanionRequestDto companionRequestDto);

  CompanionDto companionToCompanionDto(Companion companion);

  void updateCompanionFromDto(CompanionUpdateDto dto, @MappingTarget Companion entity);
}
