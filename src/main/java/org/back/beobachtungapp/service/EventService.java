package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.update.event.EventUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.mapper.EventMapper;
import org.back.beobachtungapp.repository.EventRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
  private final EventRepository eventRepository;
  private final EventMapper eventMapper;
  private final ChildService childService;

  @Transactional
  public EventResponseDto save(
      EventRequestDto eventRequestDto, CompanionDto companionDto, Long childId) {

    Event event = eventMapper.eventRequestDtoToEvent(eventRequestDto);
    Child child = childService.findChildOrThrow(childId);
    child.addEvent(event);

    Companion companionRef = new Companion();
    companionRef.setId(companionDto.id());
    event.setCompanion(companionRef);
    Event savedEvent = eventRepository.save(event);
    return eventMapper.eventToEventResponseDto(savedEvent);
  }

  @CacheEvict(value = "event", key = "#eventId")
  @Transactional
  public EventResponseDto update(EventUpdateDto eventUpdateDto, Long eventId) {
    Event event = findEventOrThrow(eventId);
    eventMapper.updateEvent(eventUpdateDto, event);
    return eventMapper.eventToEventResponseDto(event);
  }

  @CacheEvict(value = "event", key = "#eventId")
  @Transactional
  public void delete(Long eventId) {
    Event event = findEventOrThrow(eventId);
    eventRepository.delete(event);
  }

  public List<EventResponseDto> findAll(CompanionDto companionDto) {
    return eventMapper.eventToEventResponseDtoList(
        eventRepository.findByCompanionId(companionDto.id()));
  }

  public List<EventResponseDto> findByChildId(Long childId) {
    return eventMapper.eventToEventResponseDtoList(eventRepository.findEventsByChildId(childId));
  }

  @Cacheable(value = "event", key = "#eventId", unless = "#result == null")
  public EventResponseDto findById(Long eventId) {
    Event event = findEventOrThrow(eventId);
    return eventMapper.eventToEventResponseDto(event);
  }

  private Event findEventOrThrow(Long eventId) {
    return eventRepository
        .findById(eventId)
        .orElseThrow(
            () -> {
              log.error("Event not found with id: {}", eventId);
              return new EntityNotFoundException("Event not found with id: " + eventId);
            });
  }
}
