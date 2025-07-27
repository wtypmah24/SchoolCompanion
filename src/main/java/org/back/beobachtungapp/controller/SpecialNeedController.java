package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.service.SpecialNeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("need")
@RequiredArgsConstructor
public class SpecialNeedController {
  private final SpecialNeedService specialNeedService;

  @PostMapping("child/{childId}")
  public ResponseEntity<SpecialNeedResponseDto> add(
      @RequestBody SpecialNeedRequestDto need, @PathVariable long childId) {
    specialNeedService.save(need, childId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{needId}")
  public ResponseEntity<SpecialNeedResponseDto> update(
      @RequestBody SpecialNeedUpdateDto needUpdateDto, @PathVariable long needId) {
    specialNeedService.update(needUpdateDto, needId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{needId}")
  public ResponseEntity<Void> delete(@PathVariable long needId) {
    specialNeedService.delete(needId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{needId}")
  public ResponseEntity<SpecialNeedResponseDto> getById(@PathVariable long needId) {
    return ResponseEntity.status(HttpStatus.OK).body(specialNeedService.findById(needId));
  }

  @GetMapping("child/{childId}")
  public ResponseEntity<List<SpecialNeedResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(specialNeedService.findByChild(childId));
  }
}
