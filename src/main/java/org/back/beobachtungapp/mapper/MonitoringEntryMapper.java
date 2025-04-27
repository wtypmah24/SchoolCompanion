package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MonitoringEntryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "monitoringParameter", ignore = true)
    MonitoringEntry monitoringEntryRequestDtoToEntry(MonitoringEntryRequestDto monitoringEntryRequestDto);
}
