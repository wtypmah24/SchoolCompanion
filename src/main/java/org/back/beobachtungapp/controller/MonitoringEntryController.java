package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.service.MonitoringEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "MonitoringEntryService is injected and not externally exposed")
@Tag(
    name = "Monitoring entry controller",
    description = "Operations related to child's Monitoring entries management")
@RestController
@RequestMapping("entry")
public class MonitoringEntryController {
  private final MonitoringEntryService entryService;

  @Autowired
  public MonitoringEntryController(MonitoringEntryService entryService) {
    this.entryService = entryService;
  }

  @Operation(
      summary = "Add a new Monitoring entry",
      description = "Creates a new Monitoring entry record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Monitoring entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}")
  public ResponseEntity<MonitoringEntryResponseDto> add(
      @Parameter(description = "Monitoring entry details to be added") @RequestBody
          MonitoringEntryRequestDto requestDto,
      @PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(entryService.save(requestDto, childId));
  }

  @Operation(
      summary = "Update Monitoring entry",
      description = "Update Monitoring entry record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring entry updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{entryId}")
  public ResponseEntity<MonitoringEntryResponseDto> update(
      @Parameter(description = "Monitoring param details to be updated") @RequestBody
          MonitoringEntryUpdateDto updateDto,
      @PathVariable long entryId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.update(updateDto, entryId));
  }

  @Operation(
      summary = "Delete Monitoring entry",
      description = "Delete Monitoring entry record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring entry deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{entryId}")
  public ResponseEntity<Void> delete(@PathVariable long entryId) {
    entryService.delete(entryId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get Monitoring entry by ID",
      description = "Retrieve Monitoring entry by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Monitoring entry retrieved successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "No Monitoring entry found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{entryId}")
  public ResponseEntity<MonitoringEntryResponseDto> getById(@PathVariable long entryId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findById(entryId));
  }

  @Operation(
      summary = "Get all Monitoring entries",
      description = "Retrieve all Monitoring entries associated with a child.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of Monitoring entries retrieved successfully"),
        @ApiResponse(
            responseCode = "404",
            description = "No Monitoring entries found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{childId}")
  public ResponseEntity<List<MonitoringEntryResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findAll(childId));
  }
}
