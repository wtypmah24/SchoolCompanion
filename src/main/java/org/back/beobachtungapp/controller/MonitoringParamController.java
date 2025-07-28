package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.service.MonitoringParamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("param")
@RequiredArgsConstructor
public class MonitoringParamController {
  private final MonitoringParamService paramService;

  @PostMapping()
  public ResponseEntity<MonitoringParamResponseDto> add(
      @RequestBody MonitoringParamRequestDto requestDto,
      @CurrentCompanion CompanionDto companionDto) {
    paramService.save(requestDto, companionDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{paramId}")
  public ResponseEntity<MonitoringParamResponseDto> update(
      @RequestBody MonitoringParamUpdateDto updateDto,
      @PathVariable long paramId,
      @CurrentCompanion CompanionDto companionDto) {
    paramService.update(updateDto, paramId, companionDto);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{paramId}")
  public ResponseEntity<Void> delete(
      @PathVariable long paramId, @CurrentCompanion CompanionDto companionDto) {
    paramService.delete(paramId, companionDto);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{paramId}")
  public ResponseEntity<MonitoringParamResponseDto> getById(@PathVariable long paramId) {
    return ResponseEntity.status(HttpStatus.OK).body(paramService.findById(paramId));
  }

  @GetMapping()
  public ResponseEntity<List<MonitoringParamResponseDto>> getAll(
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(paramService.findAll(companionDto));
  }
}
