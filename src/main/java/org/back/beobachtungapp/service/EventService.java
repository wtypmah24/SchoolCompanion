package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.update.event.EventUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.event.Event;
import org.back.beobachtungapp.mapper.EventMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EventService {
  private final EventRepository eventRepository;
  private final EventMapper eventMapper;
  private final ChildRepository childRepository;

  @Autowired
  public EventService(
      EventRepository eventRepository, EventMapper eventMapper, ChildRepository childRepository) {
    this.eventRepository = eventRepository;
    this.eventMapper = eventMapper;
    this.childRepository = childRepository;
  }

  @Transactional
  public EventResponseDto save(
      EventRequestDto eventRequestDto, CompanionDto companionDto, Long childId) {
    log.info(
        "Saving new event with title: {}, companion: {} and child: {}",
        eventRequestDto.title(),
        companionDto.id(),
        childId);

    Event event = eventMapper.eventRequestDtoToEvent(eventRequestDto);
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
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
    log.info("Updating event with id: {}", eventId);

    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(
                () -> {
                  log.error("Event not found with id: {}", eventId);
                  return new EntityNotFoundException("Event not found with id: " + eventId);
                });

    eventMapper.updateEvent(eventUpdateDto, event);
    log.info("Event with id: {} successfully updated", eventId);
    return eventMapper.eventToEventResponseDto(event);
  }

  @CacheEvict(value = "event", key = "#eventId")
  @Transactional
  public void delete(Long eventId) {
    log.info("Deleting event with id: {}", eventId);

    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(
                () -> {
                  log.error("Event not found with id: {}", eventId);
                  return new EntityNotFoundException("Event not found with id: " + eventId);
                });

    eventRepository.delete(event);
    log.info("Event with id: {} successfully deleted", eventId);
  }

  public List<EventResponseDto> findAll(CompanionDto companionDto) {
    log.info("Fetching all events for companion with id: {}", companionDto.id());

    List<EventResponseDto> events =
        eventMapper.eventToEventResponseDtoList(
            eventRepository.findByCompanionId(companionDto.id()));

    log.info("Found {} events for companion with id: {}", events.size(), companionDto.id());

    return events;
  }

  public List<EventResponseDto> findByChildId(Long childId) {
    log.info("Fetching all events for child with id: {}", childId);

    List<EventResponseDto> events =
        eventMapper.eventToEventResponseDtoList(eventRepository.findEventsByChildId(childId));

    log.info("Found {} events for child with id: {}", events.size(), childId);

    return events;
  }

  @Cacheable(value = "event", key = "#eventId", unless = "#result == null")
  public EventResponseDto findById(Long eventId) {
    log.info("Fetching event with id: {}", eventId);

    Event event =
        eventRepository
            .findById(eventId)
            .orElseThrow(
                () -> {
                  log.error("Event not found with id: {}", eventId);
                  return new EntityNotFoundException("Event not found with id: " + eventId);
                });

    log.info("Found event with id: {}", eventId);
    return eventMapper.eventToEventResponseDto(event);
  }
}
