package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.event.CacheEvent;
import org.back.beobachtungapp.mapper.MonitoringParamMapper;
import org.back.beobachtungapp.repository.MonitoringParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MonitoringParamService {
  private final MonitoringParamRepository monitoringParamRepository;
  private final MonitoringParamMapper monitoringParamMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public MonitoringParamService(
      MonitoringParamRepository monitoringParamRepository,
      MonitoringParamMapper monitoringParamMapper,
      ApplicationEventPublisher eventPublisher) {
    this.monitoringParamRepository = monitoringParamRepository;
    this.monitoringParamMapper = monitoringParamMapper;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public MonitoringParamResponseDto save(
      MonitoringParamRequestDto requestDto, CompanionDto companionDto) {
    log.info("Saving new monitoring parameter for companion with id: {}", companionDto.id());

    MonitoringParameter param =
        monitoringParamMapper.monitoringParamRequestDtoToMonitoringParam(requestDto);
    Companion companion = new Companion();
    companion.setId(companionDto.id());
    param.setCompanion(companion);

    MonitoringParameter savedParam = monitoringParamRepository.save(param);
    log.info(
        "Successfully saved monitoring parameter with id: {} for companion with id: {}",
        savedParam.getId(),
        companionDto.id());

    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(savedParam);
  }

  @Transactional
  public MonitoringParamResponseDto update(MonitoringParamUpdateDto updateDto, Long paramId) {
    log.info("Updating monitoring parameter with id: {}", paramId);

    MonitoringParameter param =
        monitoringParamRepository
            .findById(paramId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring param not found with id: {}", paramId);
                  return new EntityNotFoundException(
                      "Monitoring param not found with id: " + paramId);
                });

    monitoringParamMapper.updateMonitoringParam(updateDto, param);
    log.info("Successfully updated monitoring param with id: {}", paramId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"param", "params"},
            new Object[] {paramId, param.getCompanion().getId()}));

    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(param);
  }

  @Transactional
  public void delete(Long paramId) {
    log.info("Deleting monitoring parameter with id: {}", paramId);

    MonitoringParameter param =
        monitoringParamRepository
            .findById(paramId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring param not found with id: {}", paramId);
                  return new EntityNotFoundException(
                      "Monitoring param not found with id: " + paramId);
                });

    monitoringParamRepository.delete(param);
    log.info("Successfully deleted monitoring parameter with id: {}", paramId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"param", "params"},
            new Object[] {paramId, param.getCompanion().getId()}));
  }

  @Cacheable(value = "params", key = "#companionDto.id()")
  public List<MonitoringParamResponseDto> findAll(CompanionDto companionDto) {
    log.info("Fetching all monitoring parameters for companion with id: {}", companionDto.id());

    List<MonitoringParamResponseDto> params =
        monitoringParamMapper.monitoringParamsToMonitoringsDtoList(
            monitoringParamRepository.findByCompanionId(companionDto.id()));

    log.info(
        "Found {} monitoring parameters for companion with id: {}",
        params.size(),
        companionDto.id());

    return params;
  }

  @Cacheable(value = "param", key = "#paramId")
  public MonitoringParamResponseDto findById(Long paramId) {
    log.info("Fetching monitoring parameter with id: {}", paramId);

    MonitoringParameter param =
        monitoringParamRepository
            .findById(paramId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring param not found with id: {}", paramId);
                  return new EntityNotFoundException(
                      "Monitoring param not found with id: " + paramId);
                });

    log.info("Found monitoring parameter with id: {}", paramId);
    return monitoringParamMapper.monitoringParamToMonitoringParamResponseDto(param);
  }
}
