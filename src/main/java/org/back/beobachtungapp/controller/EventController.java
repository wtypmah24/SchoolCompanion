package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.update.event.EventUpdateDto;
import org.back.beobachtungapp.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("event")
@RequiredArgsConstructor
public class EventController {
  private final EventService eventService;

  @PostMapping("child/{childId}")
  public ResponseEntity<Void> add(
      @PathVariable("childId") Long childId,
      @RequestBody EventRequestDto eventRequestDto,
      @CurrentCompanion CompanionDto companion) {
    eventService.save(eventRequestDto, companion, childId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{eventId}/child/{childId}")
  public ResponseEntity<EventResponseDto> update(
      @RequestBody EventUpdateDto eventUpdateDto,
      @PathVariable long eventId,
      @PathVariable Long childId,
      @CurrentCompanion CompanionDto companion) {
    eventService.update(eventUpdateDto, eventId, companion, childId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{eventId}/child/{childId}")
  public ResponseEntity<Void> delete(
      @PathVariable long eventId,
      @PathVariable long childId,
      @CurrentCompanion CompanionDto companion) {
    eventService.delete(eventId, companion, childId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{eventId}")
  public ResponseEntity<EventResponseDto> getById(@PathVariable long eventId) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findById(eventId));
  }

  @GetMapping()
  public ResponseEntity<List<EventResponseDto>> getAll(
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findAll(companionDto));
  }

  @GetMapping("child/{childId}")
  public ResponseEntity<List<EventResponseDto>> getByChildId(@PathVariable long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findByChild(childId));
  }
}
