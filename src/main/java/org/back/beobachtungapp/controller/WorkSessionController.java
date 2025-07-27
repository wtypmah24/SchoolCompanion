package org.back.beobachtungapp.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("work-session")
public class WorkSessionController {
  private final WorkSessionService sessionService;

  @GetMapping("start")
  public ResponseEntity<Void> start(@CurrentCompanion CompanionDto companion) {
    sessionService.startWorkSession(companion);
    return ResponseEntity.ok().build();
  }

  @GetMapping("end")
  public ResponseEntity<Void> end(@CurrentCompanion CompanionDto companion) {
    sessionService.endWorkSession(companion);
    return ResponseEntity.ok().build();
  }

  @GetMapping("status")
  public ResponseEntity<WorkSessionResponseDto> status(@CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(sessionService.isWorking(companion));
  }

  @GetMapping("reports")
  public ResponseEntity<List<WorkSessionResponseDto>> report(
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(sessionService.getWorkSessionsByDates(companion, startDate, endDate));
  }
}
