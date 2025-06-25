package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.mapper.MonitoringEntryMapper;
import org.back.beobachtungapp.repository.MonitoringEntryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringEntryService {
  private final MonitoringEntryRepository monitoringEntryRepository;
  private final MonitoringEntryMapper monitoringEntryMapper;
  private final ChildService childService;

  @Transactional
  public MonitoringEntryResponseDto save(
      MonitoringEntryRequestDto requestDto, Long childId, Long paramId) {

    MonitoringEntry entry = monitoringEntryMapper.monitoringEntryRequestDtoToEntry(requestDto);
    Child child = childService.findChildOrThrow(childId);
    MonitoringParameter param = new MonitoringParameter();
    param.setId(paramId);
    child.addMonitoringEntry(entry);
    entry.setMonitoringParameter(param);
    MonitoringEntry savedEntry = monitoringEntryRepository.save(entry);
    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(savedEntry);
  }

  @CacheEvict(value = "entry", key = "#entryId")
  @Transactional
  public MonitoringEntryResponseDto update(MonitoringEntryUpdateDto updateDto, Long entryId) {
    MonitoringEntry entry = findEntryOrThrow(entryId);
    monitoringEntryMapper.updateMonitoringEntry(updateDto, entry);
    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(entry);
  }

  @CacheEvict(value = "entry", key = "#entryId")
  @Transactional
  public void delete(Long entryId) {
    MonitoringEntry entry = findEntryOrThrow(entryId);
    monitoringEntryRepository.delete(entry);
  }

  public List<MonitoringEntryResponseDto> findAllByChildId(Long childId) {
    return monitoringEntryMapper.monitoringEntriesToMonitoringsDtoList(
        monitoringEntryRepository.findByChildId(childId));
  }

  @Cacheable(value = "entry", key = "#entryId", unless = "#result == null")
  public MonitoringEntryResponseDto findById(Long entryId) {
    MonitoringEntry entry = findEntryOrThrow(entryId);
    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(entry);
  }

  public List<MonitoringEntryResponseDto> findAll() {
    return monitoringEntryMapper.monitoringEntriesToMonitoringsDtoList(
        monitoringEntryRepository.findAll());
  }

  private MonitoringEntry findEntryOrThrow(Long entryId) {
    return monitoringEntryRepository
        .findById(entryId)
        .orElseThrow(
            () -> {
              log.error("Monitoring entry not found with id: {}", entryId);
              return new EntityNotFoundException("Monitoring entry not found with id: " + entryId);
            });
  }
}
