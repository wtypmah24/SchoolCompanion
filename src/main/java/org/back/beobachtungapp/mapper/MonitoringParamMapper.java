package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonitoringParamMapper {

  MonitoringParameter monitoringParamRequestDtoToMonitoringParam(
      MonitoringParamRequestDto monitoringParameter);

  MonitoringParamResponseDto monitoringParamToMonitoringParamResponseDto(MonitoringParameter param);

  List<MonitoringParamResponseDto> monitoringParamsToMonitoringsDtoList(
      List<MonitoringParameter> params);

  void updateMonitoringParam(
      MonitoringParamUpdateDto dto, @MappingTarget MonitoringParameter param);
}
