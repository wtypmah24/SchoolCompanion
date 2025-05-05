package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.request.event.EventUpdateDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "EventService is injected and not externally exposed")
@Tag(name = "Event controller", description = "Operations related to companion's events management")
@RestController
@RequestMapping("event")
public class EventController {
  private final EventService eventService;

  @Autowired
  public EventController(EventService eventService) {
    this.eventService = eventService;
  }

  @Operation(
      summary = "Add a new event",
      description = "Creates a new event record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Event created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}")
  public ResponseEntity<EventResponseDto> add(
      @PathVariable("childId") Long childId,
      @Parameter(description = "Event details to be added") @RequestBody
          EventRequestDto eventRequestDto,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(eventService.save(eventRequestDto, companion, childId));
  }

  @Operation(
      summary = "Update event",
      description = "Update event record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{eventId}")
  public ResponseEntity<EventResponseDto> update(
      @Parameter(description = "Event details to be updated") @RequestBody
          EventUpdateDto eventUpdateDto,
      @PathVariable long eventId) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.update(eventUpdateDto, eventId));
  }

  @Operation(
      summary = "Delete event",
      description = "Delete event record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{eventId}")
  public ResponseEntity<Void> delete(@PathVariable long eventId) {
    eventService.delete(eventId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get event by ID",
      description = "Retrieve event by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No event found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{eventId}")
  public ResponseEntity<EventResponseDto> getById(@PathVariable long eventId) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findById(eventId));
  }

  @Operation(
      summary = "Get all events",
      description = "Retrieve all events associated with a companion.",
      responses = {
        @ApiResponse(responseCode = "200", description = "List of events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No events found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping()
  public ResponseEntity<List<EventResponseDto>> getAll(
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findAll(companionDto));
  }

  @Operation(
      summary = "Get events by child ID",
      description = "Retrieve events by child ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No event found with provided child ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{childId}")
  public ResponseEntity<List<EventResponseDto>> getByChildId(@PathVariable long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(eventService.findByChildId(childId));
  }
}
