package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.dto.update.task.TaskUpdateDto;
import org.back.beobachtungapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
public class TaskController {
  private final TaskService taskService;

  @PostMapping("child/{childId}")
  public ResponseEntity<TaskResponseDto> add(
      @PathVariable("childId") Long childId,
      @RequestBody TaskRequestDto taskRequestDto,
      @CurrentCompanion CompanionDto companion) {
    taskService.save(taskRequestDto, companion, childId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{taskId}/child/{childId}")
  public ResponseEntity<TaskResponseDto> update(
      @RequestBody TaskUpdateDto taskUpdateDto,
      @PathVariable long taskId,
      @PathVariable long childId) {
    taskService.update(taskUpdateDto, taskId, childId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{taskId}/ child/{childId}")
  public ResponseEntity<Void> delete(@PathVariable long taskId, @PathVariable long childId) {
    taskService.delete(taskId, childId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{taskId}")
  public ResponseEntity<TaskResponseDto> getById(@PathVariable long taskId) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findById(taskId));
  }

  @GetMapping()
  public ResponseEntity<List<TaskResponseDto>> getAll(@CurrentCompanion CompanionDto companionDto) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findAll(companionDto));
  }

  @GetMapping("child/{taskId}")
  public ResponseEntity<List<TaskResponseDto>> getByChildId(@PathVariable long taskId) {
    return ResponseEntity.status(HttpStatus.OK).body(taskService.findByChildId(taskId));
  }
}
