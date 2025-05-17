package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpecialNeedMapper {

  SpecialNeed specialNeedRequestDtoToSpecialNeed(SpecialNeedRequestDto specialNeedRequestDto);

  SpecialNeedResponseDto specialNeedToSpecialNeedResponseDto(SpecialNeed specialNeed);

  List<SpecialNeedResponseDto> specialNeedToSpecialNeedResponseDtoList(
      List<SpecialNeed> specialNeeds);

  void updateSpecialNeedFromDto(SpecialNeedUpdateDto dto, @MappingTarget SpecialNeed need);
}
