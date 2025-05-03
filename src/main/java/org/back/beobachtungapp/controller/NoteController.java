package org.back.beobachtungapp.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.back.beobachtungapp.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "NoteService is injected and not externally exposed")
@Tag(name = "Note controller", description = "Operations related to child's notes management")
@RestController
@RequestMapping("note")
public class NoteController {
  private final NoteService noteService;

  @Autowired
  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @Operation(
      summary = "Add a new note",
      description = "Creates a new note record.",
      responses = {
        @ApiResponse(responseCode = "201", description = "Note created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PostMapping("child/{childId}")
  public ResponseEntity<NoteResponseDto> add(
      @Parameter(description = "Note details to be added") @RequestBody
          NoteRequestDto noteRequestDto,
      @PathVariable long childId,
      @CurrentCompanion CompanionDto companion) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(noteService.save(noteRequestDto, childId, companion));
  }

  @Operation(
      summary = "Update note",
      description = "Update note record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Note updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @PatchMapping("{noteId}")
  public ResponseEntity<NoteResponseDto> update(
      @Parameter(description = "Note details to be updated") @RequestBody
          NoteRequestDto noteRequestDto,
      @PathVariable long noteId) {
    return ResponseEntity.status(HttpStatus.OK).body(noteService.update(noteRequestDto, noteId));
  }

  @Operation(
      summary = "Delete note",
      description = "Delete note record.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Note deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @DeleteMapping("{noteId}")
  public ResponseEntity<Void> delete(@PathVariable long noteId) {
    noteService.delete(noteId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get note by ID",
      description = "Retrieve note by ID.",
      responses = {
        @ApiResponse(responseCode = "200", description = "Note retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No note found with provided ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("{noteId}")
  public ResponseEntity<NoteResponseDto> getById(@PathVariable long noteId) {
    return ResponseEntity.status(HttpStatus.OK).body(noteService.findById(noteId));
  }

  @Operation(
      summary = "Get all notes",
      description = "Retrieve all notes associated with a child.",
      responses = {
        @ApiResponse(responseCode = "200", description = "List of notes retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No notes found for the child"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
      })
  @GetMapping("child/{childId}")
  public ResponseEntity<List<NoteResponseDto>> getAll(@PathVariable Long childId) {
    return ResponseEntity.status(HttpStatus.OK).body(noteService.findAll(childId));
  }
}
