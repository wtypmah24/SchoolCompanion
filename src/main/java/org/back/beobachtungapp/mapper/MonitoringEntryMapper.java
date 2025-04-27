package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonitoringEntryMapper {
    MonitoringEntry monitoringEntryRequestDtoToEntry(MonitoringEntryRequestDto monitoringEntryRequestDto);
}
