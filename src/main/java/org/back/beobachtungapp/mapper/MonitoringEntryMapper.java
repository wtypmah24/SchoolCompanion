package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonitoringEntryMapper {

  MonitoringEntry monitoringEntryRequestDtoToEntry(
      MonitoringEntryRequestDto monitoringEntryRequestDto);

  MonitoringEntryResponseDto monitoringEntryToMonitoringEntryResponseDto(MonitoringEntry entry);

  List<MonitoringEntryResponseDto> monitoringEntriesToMonitoringsDtoList(
      List<MonitoringEntry> entries);

  void updateMonitoringEntry(MonitoringEntryUpdateDto dto, @MappingTarget MonitoringEntry entry);
}
