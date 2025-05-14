package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.note.Note;
import org.back.beobachtungapp.mapper.NoteMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class NoteService {
  private final NoteRepository noteRepository;
  private final NoteMapper noteMapper;
  private final ChildRepository childRepository;

  @Autowired
  public NoteService(
      NoteRepository noteRepository, NoteMapper noteMapper, ChildRepository childRepository) {
    this.noteRepository = noteRepository;
    this.noteMapper = noteMapper;
    this.childRepository = childRepository;
  }

  @Transactional
  public NoteResponseDto save(
      NoteRequestDto noteRequestDto, Long childId, CompanionDto companionDto) {
    log.info(
        "Saving new note for child with id: {} and companion with id: {}",
        childId,
        companionDto.id());

    Note note = noteMapper.noteRequestDtoToNote(noteRequestDto);

    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));

    child.addNote(note);

    Note savedNote = noteRepository.save(note);
    log.info(
        "Successfully saved note with id: {} for child with id: {} and companion with id: {}",
        savedNote.getId(),
        childId,
        companionDto.id());
    return noteMapper.noteToNoteResponseDto(savedNote);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public NoteResponseDto update(NoteRequestDto noteRequestDto, Long noteId) {
    log.info("Updating note with id: {}", noteId);

    Note note =
        noteRepository
            .findById(noteId)
            .orElseThrow(
                () -> {
                  log.error("Note not found with id: {}", noteId);
                  return new EntityNotFoundException("Note not found with id: " + noteId);
                });

    noteMapper.updateNote(noteRequestDto, note);
    log.info("Successfully updated note with id: {}", noteId);
    return noteMapper.noteToNoteResponseDto(note);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public void delete(Long noteId) {
    log.info("Deleting note with id: {}", noteId);

    Note note =
        noteRepository
            .findById(noteId)
            .orElseThrow(
                () -> {
                  log.error("Note not found with id: {}", noteId);
                  return new EntityNotFoundException("Note not found with id: " + noteId);
                });

    noteRepository.delete(note);
    log.info("Successfully deleted note with id: {}", noteId);
  }

  public List<NoteResponseDto> findAll(Long childId) {
    log.info("Fetching all notes for child with id: {}", childId);

    List<NoteResponseDto> notes =
        noteMapper.noteToNotesResponseDtoList(noteRepository.findByChildId(childId));

    log.info("Found {} notes for child with id: {}", notes.size(), childId);

    return notes;
  }

  @Cacheable(value = "note", key = "#noteId", unless = "#result == null")
  public NoteResponseDto findById(Long noteId) {
    log.info("Fetching note with id: {}", noteId);

    Note note =
        noteRepository
            .findById(noteId)
            .orElseThrow(
                () -> {
                  log.error("Note not found with id: {}", noteId);
                  return new EntityNotFoundException("Note not found with id: " + noteId);
                });

    log.info("Found note with id: {}", noteId);
    return noteMapper.noteToNoteResponseDto(note);
  }
}
