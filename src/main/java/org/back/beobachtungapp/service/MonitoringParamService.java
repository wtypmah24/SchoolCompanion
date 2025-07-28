package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.ParamDao;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringParamUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringParamService {
  private final ParamDao paramDao;

  @Transactional
  @CacheEvict(value = "params", key = "#companionDto.id()")
  public void save(MonitoringParamRequestDto requestDto, CompanionDto companionDto) {
    paramDao.save(requestDto, companionDto.id());
  }

  @Caching(
      evict = {
        @CacheEvict(value = "param", key = "#paramId"),
        @CacheEvict(value = "params", key = "#companionDto.id()")
      })
  @Transactional
  public void update(MonitoringParamUpdateDto updateDto, Long paramId, CompanionDto companionDto) {
    paramDao.update(updateDto, paramId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "param", key = "#paramId"),
        @CacheEvict(value = "params", key = "#companionDto.id()")
      })
  @Transactional
  public void delete(Long paramId, CompanionDto companionDto) {
    paramDao.delete(paramId);
  }

  @Cacheable(value = "params", key = "#companionDto.id()", unless = "#result == null")
  public List<MonitoringParamResponseDto> findAll(CompanionDto companionDto) {
    return paramDao.findByCompanionId(companionDto.id());
  }

  @Cacheable(value = "param", key = "#paramId", unless = "#result == null")
  public MonitoringParamResponseDto findById(Long paramId) {
    return paramDao
        .findById(paramId)
        .orElseThrow(
            () -> {
              log.error("Monitoring param not found with id: {}", paramId);
              return new BadRequestException("Monitoring param not found with id: " + paramId);
            });
  }
}
