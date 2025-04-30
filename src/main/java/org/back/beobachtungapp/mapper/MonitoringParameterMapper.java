package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringParameterRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonitoringParameterMapper {

  MonitoringParameter monitoringParameterRequestDtoToParameter(
      MonitoringParameterRequestDto monitoringParameter);
}
