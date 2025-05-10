package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.request.event.EventUpdateDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.entity.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

  Event eventRequestDtoToEvent(EventRequestDto eventRequestDto);

  @Mapping(source = "child.id", target = "childId")
  EventResponseDto eventToEventResponseDto(Event event);

  List<EventResponseDto> eventToEventResponseDtoList(List<Event> events);

  void updateEvent(EventUpdateDto dto, @MappingTarget Event event);
}
