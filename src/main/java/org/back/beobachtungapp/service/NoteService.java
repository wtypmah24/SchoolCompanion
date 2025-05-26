package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.note.Note;
import org.back.beobachtungapp.mapper.NoteMapper;
import org.back.beobachtungapp.repository.NoteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
  private final NoteRepository noteRepository;
  private final NoteMapper noteMapper;
  private final ChildService childService;

  @Transactional
  public NoteResponseDto save(
      NoteRequestDto noteRequestDto, Long childId, CompanionDto companionDto) {

    Note note = noteMapper.noteRequestDtoToNote(noteRequestDto);

    Child child = childService.findChildOrThrow(childId);

    child.addNote(note);

    Note savedNote = noteRepository.save(note);
    return noteMapper.noteToNoteResponseDto(savedNote);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public NoteResponseDto update(NoteRequestDto noteRequestDto, Long noteId) {
    Note note = findNoteOrThrow(noteId);
    noteMapper.updateNote(noteRequestDto, note);
    return noteMapper.noteToNoteResponseDto(note);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public void delete(Long noteId) {
    Note note = findNoteOrThrow(noteId);
    noteRepository.delete(note);
  }

  public List<NoteResponseDto> findAll(Long childId) {
    return noteMapper.noteToNotesResponseDtoList(noteRepository.findByChildId(childId));
  }

  @Cacheable(value = "note", key = "#noteId", unless = "#result == null")
  public NoteResponseDto findById(Long noteId) {
    Note note = findNoteOrThrow(noteId);
    return noteMapper.noteToNoteResponseDto(note);
  }

  private Note findNoteOrThrow(Long noteId) {
    return noteRepository
        .findById(noteId)
        .orElseThrow(
            () -> {
              log.error("Note not found with id: {}", noteId);
              return new EntityNotFoundException("Note not found with id: " + noteId);
            });
  }
}
