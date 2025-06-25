package org.back.beobachtungapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.session.WorkSessionResponseDto;
import org.back.beobachtungapp.service.WorkSessionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "WorkSession controller",
    description = "Operations related to companion's work session management")
@RestController
@RequiredArgsConstructor
@RequestMapping("work-session")
public class WorkSessionController {
  private final WorkSessionService sessionService;

  @Operation(
      summary = "Start a new work session",
      description = "Start a new work session.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Work session started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("start")
  public ResponseEntity<Void> start(@CurrentCompanion CompanionDto companion) {
    sessionService.startWorkSession(companion);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "End a new work session",
      description = "End a new work session.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Work session finished successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("end")
  public ResponseEntity<Void> end(@CurrentCompanion CompanionDto companion) {
    sessionService.endWorkSession(companion);
    return ResponseEntity.ok().build();
  }

  // TODO: Swagger
  @GetMapping("status")
  public ResponseEntity<WorkSessionResponseDto> status(@CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(sessionService.isWorking(companion));
  }

  // TODO: Swagger
  @GetMapping("reports")
  public ResponseEntity<List<WorkSessionResponseDto>> report(
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(sessionService.getWorkSessionsByDates(companion, startDate, endDate));
  }
}
