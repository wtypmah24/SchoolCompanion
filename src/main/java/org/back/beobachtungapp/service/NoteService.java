package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.NoteDao;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {
  private final NoteDao noteDao;

  @Transactional
  public void save(NoteRequestDto noteRequestDto, Long childId) {
    noteDao.save(noteRequestDto, childId);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public void update(NoteRequestDto noteRequestDto, Long noteId) {
    noteDao.update(noteRequestDto, noteId);
  }

  @CacheEvict(value = "note", key = "#noteId")
  @Transactional
  public void delete(Long noteId) {
    noteDao.delete(noteId);
  }

  public List<NoteResponseDto> findByChild(Long childId) {
    return noteDao.findByChildId(childId);
  }

  @Cacheable(value = "note", key = "#noteId", unless = "#result == null")
  public NoteResponseDto findById(Long noteId) {
    return noteDao
        .findById(noteId)
        .orElseThrow(
            () -> {
              log.error("Note not found with id: {}", noteId);
              return new BadRequestException("Note not found with id: " + noteId);
            });
  }
}
