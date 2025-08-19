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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
  private final EventDao eventDao;

  @Transactional
  //  @Caching(
  //      evict = {
  //        @CacheEvict(value = "events", key = "#companionDto.id()"),
  //        @CacheEvict(value = "events_by_child", key = "#childId")
  //      })
  public void save(EventRequestDto eventRequestDto, CompanionDto companionDto, Long childId) {
    eventDao.save(eventRequestDto, childId, companionDto.id());
  }

  //  @Caching(
  //      evict = {
  //        @CacheEvict(value = "event", key = "#eventId"),
  //        @CacheEvict(value = "events", key = "#companionDto.id()"),
  //        @CacheEvict(value = "events_by_child", key = "#childId")
  //      })
  @Transactional
  public void update(
      EventUpdateDto eventUpdateDto, Long eventId, CompanionDto companionDto, Long childId) {
    eventDao.update(eventUpdateDto, eventId);
  }

  //  @Caching(
  //      evict = {
  //        @CacheEvict(value = "event", key = "#eventId"),
  //        @CacheEvict(value = "events", key = "#companionDto.id()"),
  //        @CacheEvict(value = "events_by_child", key = "#childId")
  //      })
  @Transactional
  public void delete(Long eventId, CompanionDto companionDto, Long childId) {
    eventDao.delete(eventId);
  }

  //  @Cacheable(value = "events", key = "#companionDto.id()", unless = "#result == null")
  public List<EventResponseDto> findAll(CompanionDto companionDto) {
    return eventDao.findByCompanionId(companionDto.id());
  }

  //  @Cacheable(value = "events_by_child", key = "#childId", unless = "#result == null")
  public List<EventResponseDto> findByChild(Long childId) {
    return eventDao.findByChildId(childId);
  }

  //  @Cacheable(value = "event", key = "#eventId", unless = "#result == null")
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
