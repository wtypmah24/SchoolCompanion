package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.mapper.MonitoringParamMapper;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.back.beobachtungapp.repository.MonitoringParamRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringParamService {
  private final MonitoringParamRepository monitoringParamRepository;
  private final MonitoringParamMapper monitoringParamMapper;
  private final CompanionRepository companionRepository;

  @Transactional
  public MonitoringParamResponseDto save(
      MonitoringParamRequestDto requestDto, CompanionDto companionDto) {

    MonitoringParameter param =
        monitoringParamMapper.monitoringParamRequestDtoToMonitoringParam(requestDto);
    Companion companion = companionRepository.getReferenceById(companionDto.id());
    param.setCompanion(companion);

    MonitoringParameter savedParam = monitoringParamRepository.save(param);
    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(savedParam);
  }

  @CacheEvict(value = "param", key = "#paramId")
  @Transactional
  public MonitoringParamResponseDto update(MonitoringParamUpdateDto updateDto, Long paramId) {
    MonitoringParameter param = findParamOrThrow(paramId);
    monitoringParamMapper.updateMonitoringParam(updateDto, param);
    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(param);
  }

  @CacheEvict(value = "param", key = "#paramId")
  @Transactional
  public void delete(Long paramId) {
    MonitoringParameter param = findParamOrThrow(paramId);
    monitoringParamRepository.delete(param);
  }

  public List<MonitoringParamResponseDto> findAll(CompanionDto companionDto) {
    return monitoringParamMapper.monitoringParamsToMonitoringsDtoList(
        monitoringParamRepository.findByCompanionId(companionDto.id()));
  }

  @Cacheable(value = "param", key = "#paramId", unless = "#result == null")
  public MonitoringParamResponseDto findById(Long paramId) {
    MonitoringParameter param = findParamOrThrow(paramId);
    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(param);
  }

  private MonitoringParameter findParamOrThrow(Long paramId) {
    return monitoringParamRepository
        .findById(paramId)
        .orElseThrow(
            () -> {
              log.error("Monitoring param not found with id: {}", paramId);
              return new EntityNotFoundException("Monitoring param not found with id: " + paramId);
            });
  }
}
