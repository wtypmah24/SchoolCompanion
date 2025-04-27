package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.event.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event eventRequestDtoToEvent(EventRequestDto eventRequestDto);
}
