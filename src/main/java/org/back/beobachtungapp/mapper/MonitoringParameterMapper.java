package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringParameterRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MonitoringParameterMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "child", ignore = true)
  @Mapping(target = "monitoringEntries", ignore = true)
  MonitoringParameter monitoringParameterRequestDtoToParameter(
      MonitoringParameterRequestDto monitoringParameter);
}
