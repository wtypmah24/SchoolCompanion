package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.service.MonitoringParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "MonitoringParamService is injected and not externally exposed")
@Tag(
    name = "Monitoring param controller",
    description = "Operations related to child's Monitoring params management")
@RestController
@RequestMapping("param")
public class MonitoringParamController {
  private final MonitoringParamService paramService;

  @Autowired
  public MonitoringParamController(MonitoringParamService paramService) {
    this.paramService = paramService;
  }

  @Operation(
      summary = "Add a new Monitoring param",
      description = "Creates a new Monitoring param record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Monitoring param created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping()
  public ResponseEntity<MonitoringParamResponseDto> add(
      @Parameter(description = "Monitoring param details to be added") @RequestBody
          MonitoringParamRequestDto requestDto,
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(paramService.save(requestDto, companionDto));
  }

  @Operation(
      summary = "Update Monitoring param",
      description = "Update Monitoring param record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring param updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{paramId}")
  public ResponseEntity<MonitoringParamResponseDto> update(
      @Parameter(description = "Monitoring param details to be updated") @RequestBody
          MonitoringParamUpdateDto updateDto,
      @PathVariable long paramId) {
    return ResponseEntity.status(HttpStatus.OK).body(paramService.update(updateDto, paramId));
  }

  @Operation(
      summary = "Delete Monitoring param",
      description = "Delete Monitoring param record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring param deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{paramId}")
  public ResponseEntity<Void> delete(@PathVariable long paramId) {
    paramService.delete(paramId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get Monitoring param by ID",
      description = "Retrieve Monitoring param by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring param retrieved successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "No Monitoring param found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{paramId}")
  public ResponseEntity<MonitoringParamResponseDto> getById(@PathVariable long paramId) {
    return ResponseEntity.status(HttpStatus.OK).body(paramService.findById(paramId));
  }

  @Operation(
      summary = "Get all Monitoring params",
      description = "Retrieve all Monitoring params associated with a child.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of Monitoring params retrieved successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "No Monitoring params found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping()
  public ResponseEntity<List<MonitoringParamResponseDto>> getAll(
      @CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(paramService.findAll(companionDto));
  }
}
