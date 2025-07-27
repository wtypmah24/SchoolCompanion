package org.back.beobachtungapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.back.beobachtungapp.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("note")
@RequiredArgsConstructor
public class NoteController {
  private final NoteService noteService;

  @PostMapping("child/{childId}")
  public ResponseEntity<NoteResponseDto> add(
      @RequestBody NoteRequestDto noteRequestDto, @PathVariable long childId) {
    noteService.save(noteRequestDto, childId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PatchMapping("{noteId}")
  public ResponseEntity<NoteResponseDto> update(
      @RequestBody NoteRequestDto noteRequestDto, @PathVariable long noteId) {
    noteService.update(noteRequestDto, noteId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("{noteId}")
  public ResponseEntity<Void> delete(@PathVariable long noteId) {
    noteService.delete(noteId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("{noteId}")
  public ResponseEntity<NoteResponseDto> getById(@PathVariable long noteId) {
    return ResponseEntity.status(HttpStatus.OK).body(noteService.findById(noteId));
  }

  @GetMapping("child/{childId}")
  public ResponseEntity<List<NoteResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(noteService.findByChild(childId));
  }
}
