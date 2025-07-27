package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.EntryDao;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringEntryService {
  private final EntryDao entryDao;

  @Transactional
  public void save(MonitoringEntryRequestDto requestDto, Long childId, Long paramId) {
    entryDao.save(requestDto, childId, paramId);
  }

  @CacheEvict(value = "entry", key = "#entryId")
  @Transactional
  public void update(MonitoringEntryUpdateDto updateDto, Long entryId) {
    entryDao.update(updateDto, entryId);
  }

  @CacheEvict(value = "entry", key = "#entryId")
  @Transactional
  public void delete(Long entryId) {
    entryDao.delete(entryId);
  }

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

  public List<MonitoringEntryResponseDto> findAll() {
    return entryDao.findAll();
  }
}
