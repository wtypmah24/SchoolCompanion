package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.monitoring.MonitoringParameterRequestDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonitoringParameterMapper {
    MonitoringParameter monitoringParameterRequestDtoToParameter(MonitoringParameterRequestDto monitoringParameter);
}
