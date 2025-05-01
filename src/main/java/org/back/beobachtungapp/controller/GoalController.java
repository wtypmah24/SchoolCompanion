package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.back.beobachtungapp.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "GoalService is injected and not externally exposed")
@Tag(name = "Goal controller", description = "Operations related to child's goal management")
@RestController
@RequestMapping("goal")
public class GoalController {
  private final GoalService goalService;

  @Autowired
  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @Operation(
      summary = "Add a new goal",
      description = "Creates a new goal record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Goal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("{childId}")
  public ResponseEntity<GoalResponseDto> add(
      @Parameter(description = "Goal details to be added") @RequestBody GoalRequestDto goal,
      @PathVariable long childId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(goalService.save(goal, childId));
  }

  @Operation(
      summary = "Update goal",
      description = "Update goal record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{goalId}")
  public ResponseEntity<GoalResponseDto> update(
      @Parameter(description = "Child details to be updated") @RequestBody
          GoalRequestDto goalRequestDto,
      @PathVariable long goalId) {
    return ResponseEntity.status(HttpStatus.OK).body(goalService.update(goalRequestDto, goalId));
  }

  @Operation(
      summary = "Delete goal",
      description = "Delete goal record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{goalId}")
  public ResponseEntity<Void> delete(@PathVariable long goalId) {
    goalService.delete(goalId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get goal by ID",
      description = "Retrieve goal by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Goal retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No goal found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{goaId}")
  public ResponseEntity<GoalResponseDto> getById(@PathVariable long goaId) {
    return ResponseEntity.status(HttpStatus.OK).body(goalService.findById(goaId));
  }

  @Operation(
      summary = "Get all goals",
      description = "Retrieve all goals associated with a child.",
      responses = {
        @ApiResponse(responseCode = "200", description = "List of goals retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No goals found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{childId}")
  public ResponseEntity<List<GoalResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(goalService.findAll(childId));
  }
}
