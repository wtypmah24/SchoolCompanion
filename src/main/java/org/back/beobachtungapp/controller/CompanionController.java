package org.back.beobachtungapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
    name = "Authentication and Registration",
    description = "Operations for companion registration and login")
public class CompanionController {
  private final CompanionService companionService;

  @Operation(
      summary = "Update companion",
      description = "Update companion record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Companion updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping()
  public ResponseEntity<CompanionDto> update(
      @Parameter(description = "Companion details to be updated") @RequestBody
          CompanionUpdateDto updateDto,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(companionService.update(updateDto, companion));
  }

  @Operation(
      summary = "Delete account",
      description = "Permanently delete the authenticated companion's account.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping()
  public ResponseEntity<Void> deleteAccount(@CurrentCompanion CompanionDto companionDto) {
    companionService.delete(companionDto);
    return ResponseEntity.ok().build();
  }
}
