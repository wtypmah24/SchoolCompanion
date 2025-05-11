package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.request.task.TaskUpdateDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "TaskService is injected and not externally exposed")
@Tag(name = "Task controller", description = "Operations related to companion's tasks management")
@RestController
@RequestMapping("task")
public class TaskController {
  private final TaskService taskService;

  @Autowired
  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @Operation(
      summary = "Add a new task",
      description = "Creates a new task record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}")
  public ResponseEntity<TaskResponseDto> add(
      @PathVariable("childId") Long childId,
      @Parameter(description = "Task details to be added") @RequestBody
          TaskRequestDto taskRequestDto,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(taskService.save(taskRequestDto, companion, childId));
  }

  @Operation(
      summary = "Update task",
      description = "Update task record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{taskId}")
  public ResponseEntity<TaskResponseDto> update(
      @Parameter(description = "Event details to be updated") @RequestBody
          TaskUpdateDto taskUpdateDto,
      @PathVariable long taskId) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.update(taskUpdateDto, taskId));
  }

  @Operation(
      summary = "Delete task",
      description = "Delete task record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{taskId}")
  public ResponseEntity<Void> delete(@PathVariable long taskId) {
    taskService.delete(taskId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get task by ID",
      description = "Retrieve task by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No task found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{taskId}")
  public ResponseEntity<TaskResponseDto> getById(@PathVariable long taskId) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findById(taskId));
  }

  @Operation(
      summary = "Get all tasks",
      description = "Retrieve all tasks associated with a companion.",
      responses = {
        @ApiResponse(responseCode = "200", description = "List of tasks retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No tasks found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping()
  public ResponseEntity<List<TaskResponseDto>> getAll(@CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll(companionDto));
  }

  @Operation(
      summary = "Get tasks by child ID",
      description = "Retrieve tasks by child ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No tasks found with provided child ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{taskId}")
  public ResponseEntity<List<TaskResponseDto>> getByChildId(@PathVariable long taskId) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findByChildId(taskId));
  }
}
