package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.response.session.WorkSessionResponseDto;
import org.back.beobachtungapp.entity.session.WorkSession;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkSessionMapper {
  WorkSessionResponseDto sessionToResponseDto(WorkSession workSession);

  List<WorkSessionResponseDto> sessionListToResponseDtoList(List<WorkSession> workSessions);
}
