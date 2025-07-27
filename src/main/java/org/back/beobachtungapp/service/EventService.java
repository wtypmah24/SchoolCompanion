package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.EventDao;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.update.event.EventUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
  private final EventDao eventDao;

  @Transactional
  public void save(EventRequestDto eventRequestDto, CompanionDto companionDto, Long childId) {
    eventDao.save(eventRequestDto, childId, companionDto.id());
  }

  @CacheEvict(value = "event", key = "#eventId")
  @Transactional
  public void update(EventUpdateDto eventUpdateDto, Long eventId) {
    eventDao.update(eventUpdateDto, eventId);
  }

  @CacheEvict(value = "event", key = "#eventId")
  @Transactional
  public void delete(Long eventId) {
    eventDao.delete(eventId);
  }

  public List<EventResponseDto> findAll(CompanionDto companionDto) {
    return eventDao.findByCompanionId(companionDto.id());
  }

  public List<EventResponseDto> findByChild(Long childId) {
    return eventDao.findByChildId(childId);
  }

  @Cacheable(value = "event", key = "#eventId", unless = "#result == null")
  public EventResponseDto findById(Long eventId) {
    return eventDao
        .findById(eventId)
        .orElseThrow(
            () -> {
              log.error("Event not found with id: {}", eventId);
              return new BadRequestException("Event not found with id: " + eventId);
            });
  }
}
