package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.back.beobachtungapp.event.CacheEvent;
import org.back.beobachtungapp.mapper.MonitoringEntryMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.MonitoringEntryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MonitoringEntryService {
  private final MonitoringEntryRepository monitoringEntryRepository;
  private final MonitoringEntryMapper monitoringEntryMapper;
  private final ChildRepository childRepository;
  private final ApplicationEventPublisher eventPublisher;

  public MonitoringEntryService(
      MonitoringEntryRepository monitoringEntryRepository,
      MonitoringEntryMapper monitoringEntryMapper,
      ChildRepository childRepository,
      ApplicationEventPublisher eventPublisher) {
    this.monitoringEntryRepository = monitoringEntryRepository;
    this.monitoringEntryMapper = monitoringEntryMapper;
    this.childRepository = childRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public MonitoringEntryResponseDto save(
      MonitoringEntryRequestDto requestDto, Long childId, Long paramId) {
    log.info("Saving new monitoring entry for child with id: {}", childId);

    MonitoringEntry entry = monitoringEntryMapper.monitoringEntryRequestDtoToEntry(requestDto);
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
    MonitoringParameter param = new MonitoringParameter();
    param.setId(paramId);
    child.addMonitoringEntry(entry);
    entry.setMonitoringParameter(param);
    MonitoringEntry savedEntry = monitoringEntryRepository.save(entry);
    log.info(
        "Successfully saved monitoring entry with id: {} for child with id: {}",
        savedEntry.getId(),
        childId);

    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(savedEntry);
  }

  @Transactional
  public MonitoringEntryResponseDto update(MonitoringEntryUpdateDto updateDto, Long entryId) {
    log.info("Updating monitoring entry with id: {}", entryId);

    MonitoringEntry entry =
        monitoringEntryRepository
            .findById(entryId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring entry not found with id: {}", entryId);
                  return new EntityNotFoundException(
                      "Monitoring entry not found with id: " + entryId);
                });

    monitoringEntryMapper.updateMonitoringEntry(updateDto, entry);
    log.info("Monitoring entry with id: {} successfully updated", entryId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"entry", "entries"}, new Object[] {entryId, entry.getChild().getId()}));

    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(entry);
  }

  @Transactional
  public void delete(Long entryId) {
    log.info("Deleting monitoring entry with id: {}", entryId);

    MonitoringEntry entry =
        monitoringEntryRepository
            .findById(entryId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring entry not found with id: {}", entryId);
                  return new EntityNotFoundException(
                      "Monitoring entry not found with id: " + entryId);
                });

    monitoringEntryRepository.delete(entry);
    log.info("Successfully deleted monitoring entry with id: {}", entryId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"entry", "entries"}, new Object[] {entryId, entry.getChild().getId()}));
  }

  @Cacheable(value = "entries", key = "#childId")
  public List<MonitoringEntryResponseDto> findAllByChildId(Long childId) {
    log.info("Fetching all monitoring entries for child with id: {}", childId);

    List<MonitoringEntryResponseDto> entries =
        monitoringEntryMapper.monitoringEntriesToMonitoringsDtoList(
            monitoringEntryRepository.findByChildId(childId));

    log.info("Found {} monitoring entries for child with id: {}", entries.size(), childId);

    return entries;
  }

  @Cacheable(value = "entry", key = "#entryId")
  public MonitoringEntryResponseDto findById(Long entryId) {
    log.info("Fetching monitoring entry with id: {}", entryId);

    MonitoringEntry entry =
        monitoringEntryRepository
            .findById(entryId)
            .orElseThrow(
                () -> {
                  log.error("Monitoring entry not found with id: {}", entryId);
                  return new EntityNotFoundException(
                      "Monitoring entry not found with id: " + entryId);
                });

    log.info("Found monitoring entry with id: {}", entryId);
    return monitoringEntryMapper.monitoringEntryToMonitoringEntryResponseDto(entry);
  }

  public List<MonitoringEntryResponseDto> findAll() {

    return monitoringEntryMapper.monitoringEntriesToMonitoringsDtoList(
        monitoringEntryRepository.findAll());
  }
}
