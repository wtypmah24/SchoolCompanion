package org.back.beobachtungapp.controller;

import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.service.CompanionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
public class CompanionController {
  private final CompanionService companionService;

  @PatchMapping()
  public ResponseEntity<CompanionDto> update(
      @RequestBody CompanionUpdateDto updateDto, @CurrentCompanion CompanionDto companion) {
    companionService.update(updateDto, companion);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteAccount(@CurrentCompanion CompanionDto companionDto) {
    companionService.delete(companionDto);
    return ResponseEntity.ok().build();
  }
}
