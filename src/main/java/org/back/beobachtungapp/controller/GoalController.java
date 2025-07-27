package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.back.beobachtungapp.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("goal")
@RequiredArgsConstructor
public class GoalController {
  private final GoalService goalService;

  @PostMapping("child/{childId}")
  public ResponseEntity<GoalResponseDto> add(
      @RequestBody GoalRequestDto goal, @PathVariable long childId) {
    goalService.save(goal, childId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{goalId}")
  public ResponseEntity<GoalResponseDto> update(
      @RequestBody GoalRequestDto goalRequestDto, @PathVariable long goalId) {
    goalService.update(goalRequestDto, goalId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{goalId}")
  public ResponseEntity<Void> delete(@PathVariable long goalId) {
    goalService.delete(goalId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{goaId}")
  public ResponseEntity<GoalResponseDto> getById(@PathVariable long goaId) {
    return ResponseEntity.status(HttpStatus.OK).body(goalService.findById(goaId));
  }

  @GetMapping("child/{childId}")
  public ResponseEntity<List<GoalResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(goalService.findByChild(childId));
  }
}
