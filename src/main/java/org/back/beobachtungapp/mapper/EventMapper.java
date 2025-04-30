package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.entity.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

  Event eventRequestDtoToEvent(EventRequestDto eventRequestDto);
}
