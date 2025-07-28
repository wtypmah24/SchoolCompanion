package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.child.ChildUpdateDto;
import org.back.beobachtungapp.service.ChildService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("child")
@RequiredArgsConstructor
public class ChildController {
  private final ChildService childService;

  @PostMapping()
  public ResponseEntity<ChildResponseDto> add(
      @RequestBody ChildRequestDto child, @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.CREATED).body(childService.save(child, companion));
  }

  @PatchMapping("{childId}")
  public ResponseEntity<Void> update(
      @RequestBody ChildUpdateDto child,
      @PathVariable long childId,
      @CurrentCompanion CompanionDto companion) {
    childService.update(child, childId, companion);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{childId}")
  public ResponseEntity<Void> delete(
      @PathVariable long childId, @CurrentCompanion CompanionDto companion) {
    childService.delete(childId, companion);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{childId}")
  public ResponseEntity<ChildResponseDto> getChildById(@PathVariable long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(childService.findById(childId));
  }

  @GetMapping()
  public ResponseEntity<List<ChildResponseDto>> getAll(@CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(childService.findAll(companion));
  }
}
