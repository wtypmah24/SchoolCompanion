package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonitoringEntryMapper {

  MonitoringEntry monitoringEntryRequestDtoToEntry(
      MonitoringEntryRequestDto monitoringEntryRequestDto);
}
