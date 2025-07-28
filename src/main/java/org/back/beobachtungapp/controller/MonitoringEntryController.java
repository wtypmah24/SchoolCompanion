package org.back.beobachtungapp.controller;

import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.reporting.PdfGenerator;
import org.back.beobachtungapp.service.MonitoringEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("entry")
public class MonitoringEntryController {
  private final MonitoringEntryService entryService;
  private final PdfGenerator pdfGenerator;

  @PostMapping("child/{childId}/param/{paramId}")
  public ResponseEntity<MonitoringEntryResponseDto> add(
      @RequestBody MonitoringEntryRequestDto requestDto,
      @PathVariable("childId") Long childId,
      @PathVariable("paramId") Long paramId,
      @CurrentCompanion CompanionDto companionDto) {
    entryService.save(requestDto, childId, paramId, companionDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{entryId}/child/{childId}")
  public ResponseEntity<MonitoringEntryResponseDto> update(
      @RequestBody MonitoringEntryUpdateDto updateDto,
      @PathVariable long entryId,
      @CurrentCompanion CompanionDto companionDto,
      @PathVariable Long childId) {
    entryService.update(updateDto, entryId, companionDto, childId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{entryId}/child/{childId}")
  public ResponseEntity<Void> delete(
      @PathVariable long entryId,
      @CurrentCompanion CompanionDto companionDto,
      @PathVariable long childId) {
    entryService.delete(entryId, companionDto, childId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{entryId}")
  public ResponseEntity<MonitoringEntryResponseDto> getById(@PathVariable long entryId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findById(entryId));
  }

  @GetMapping("child/{childId}")
  public ResponseEntity<List<MonitoringEntryResponseDto>> getAllByChildId(
      @PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findAllByChildId(childId));
  }

  @GetMapping()
  public ResponseEntity<List<MonitoringEntryResponseDto>> getAll(
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findAll(companionDto));
  }

  @PostMapping("/download/child/{childId}")
  public ResponseEntity<byte[]> download(
      @PathVariable Long childId, @CurrentCompanion CompanionDto companionDto) throws IOException {
    byte[] pdfBytes = pdfGenerator.generatePdf(childId, companionDto);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }
}
