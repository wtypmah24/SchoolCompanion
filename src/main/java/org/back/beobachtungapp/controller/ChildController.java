package org.back.beobachtungapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Children controller", description = "Operations related to children management")
@RestController
@RequestMapping("child")
public class ChildController {
  private final ChildService childService;

  @Autowired
  public ChildController(ChildService childService) {
    this.childService = childService;
  }

  @Operation(
      summary = "Add a new child",
      description = "Creates a new child record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Child created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping()
  public ResponseEntity<ChildResponseDto> add(
      @Parameter(description = "Child details to be added") @RequestBody ChildRequestDto child,
      @CurrentCompanion CompanionDto companion
      ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(childService.save(child, companion));
  }

  @Operation(
      summary = "Get all children",
      description = "Retrieve all children associated with a companion.",
      responses = {
        @ApiResponse(responseCode = "200", description = "List of children retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No children found for the companion"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping()
  public ResponseEntity<List<Child>> getAll(@CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.OK).body(childService.findAllByCompanion(companion));
  }
}
