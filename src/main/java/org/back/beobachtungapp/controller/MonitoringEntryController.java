package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.back.beobachtungapp.service.MonitoringEntryService;
import org.back.beobachtungapp.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
  private final PdfGeneratorService pdfGeneratorService;

  @Autowired
  public MonitoringEntryController(
      MonitoringEntryService entryService, PdfGeneratorService pdfGeneratorService) {
    this.entryService = entryService;
    this.pdfGeneratorService = pdfGeneratorService;
  }

  @Operation(
      summary = "Add a new Monitoring entry",
      description = "Creates a new Monitoring entry record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Monitoring entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}/param/{paramId}")
  public ResponseEntity<MonitoringEntryResponseDto> add(
      @Parameter(description = "Monitoring entry details to be added") @RequestBody
          MonitoringEntryRequestDto requestDto,
      @PathVariable("childId") Long childId,
      @PathVariable("paramId") Long paramId) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(entryService.save(requestDto, childId, paramId));
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
  public ResponseEntity<List<MonitoringEntryResponseDto>> getAllByChildId(
      @PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findAllByChildId(childId));
  }

  @Operation(
      summary = "Get all Monitoring entries",
      description = "Retrieve all Monitoring entries.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of Monitoring entries retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping()
  public ResponseEntity<List<MonitoringEntryResponseDto>> getAll() {
    return ResponseEntity.status(HttpStatus.OK).body(entryService.findAll());
  }

  @Operation(
      summary = "Generate and download PDF report for a child",
      description =
          "Generates a PDF report for the specified child and returns it as a downloadable file. Requires authenticated companion context.",
      parameters = {
        @Parameter(
            name = "childId",
            description = "ID of the child for whom the PDF report should be generated",
            required = true,
            example = "123")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "PDF report generated and returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "404", description = "Child not found"),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error while generating the PDF")
      })
  @PostMapping("/download/child/{childId}")
  public ResponseEntity<byte[]> download(
      @PathVariable Long childId, @CurrentCompanion CompanionDto companionDto) throws IOException {
    byte[] pdfBytes = pdfGeneratorService.generatePdf(childId, companionDto);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdfBytes);
  }
}
