package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.EntryDao;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringEntryService {
  private final EntryDao entryDao;

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "entries", key = "#companionDto.id()"),
        @CacheEvict(value = "entries_by_child", key = "#childId")
      })
  public void save(
      MonitoringEntryRequestDto requestDto, Long childId, Long paramId, CompanionDto companionDto) {
    entryDao.save(requestDto, childId, paramId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "entry", key = "#entryId"),
        @CacheEvict(value = "entries", key = "#companionDto.id()"),
        @CacheEvict(value = "entries_by_child", key = "#childId")
      })
  @Transactional
  public void update(
      MonitoringEntryUpdateDto updateDto, Long entryId, CompanionDto companionDto, long childId) {
    entryDao.update(updateDto, entryId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "entry", key = "#entryId"),
        @CacheEvict(value = "entries", key = "#companionDto.id()"),
        @CacheEvict(value = "entries_by_child", key = "#childId")
      })
  @Transactional
  public void delete(Long entryId, CompanionDto companionDto, long childId) {
    entryDao.delete(entryId);
  }

  @Cacheable(value = "entries_by_child", key = "#childId", unless = "#result == null")
  public List<MonitoringEntryResponseDto> findAllByChildId(Long childId) {
    return entryDao.findByChildId(childId);
  }

  @Cacheable(value = "entry", key = "#entryId", unless = "#result == null")
  public MonitoringEntryResponseDto findById(Long entryId) {
    return entryDao
        .findById(entryId)
        .orElseThrow(
            () -> {
              log.error("Monitoring entry not found with id: {}", entryId);
              return new BadRequestException("Monitoring entry not found with id: " + entryId);
            });
  }

  @Cacheable(value = "entries", key = "#dto.id()", unless = "#result == null")
  public List<MonitoringEntryResponseDto> findAll(CompanionDto dto) {
    return entryDao.findAll(dto.id());
  }
}
