package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.entity.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "companion", ignore = true)
  Event eventRequestDtoToEvent(EventRequestDto eventRequestDto);
}
