package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.request.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.service.SpecialNeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "SpecialNeedService is injected and not externally exposed")
@Tag(name = "Goal controller", description = "Operations related to child's goal management")
@RestController
@RequestMapping("need")
public class SpecialNeedController {
  private final SpecialNeedService specialNeedService;

  @Autowired
  public SpecialNeedController(SpecialNeedService specialNeedService) {
    this.specialNeedService = specialNeedService;
  }

  @Operation(
      summary = "Add a new special need",
      description = "Creates a new special need record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "SpecialNeed created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}")
  public ResponseEntity<SpecialNeedResponseDto> add(
      @Parameter(description = "Special need details to be added") @RequestBody
          SpecialNeedRequestDto need,
      @PathVariable long childId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(specialNeedService.save(need, childId));
  }

  @Operation(
      summary = "Update special need",
      description = "Update special need record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Special need updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{needId}")
  public ResponseEntity<SpecialNeedResponseDto> update(
      @Parameter(description = "Needs details to be updated") @RequestBody
          SpecialNeedUpdateDto needUpdateDto,
      @PathVariable long needId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(specialNeedService.update(needUpdateDto, needId));
  }

  @Operation(
      summary = "Delete special need",
      description = "Delete special need record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Special need deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{needId}")
  public ResponseEntity<Void> delete(@PathVariable long needId) {
    specialNeedService.delete(needId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get special need by ID",
      description = "Retrieve special need by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Special need retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No special need found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{needId}")
  public ResponseEntity<SpecialNeedResponseDto> getById(@PathVariable long needId) {
    return ResponseEntity.status(HttpStatus.OK).body(specialNeedService.findById(needId));
  }

  @Operation(
      summary = "Get all special needs",
      description = "Retrieve all special need associated with a child.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of special needs retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No special needs found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{childId}")
  public ResponseEntity<List<SpecialNeedResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(specialNeedService.findAll(childId));
  }
}
