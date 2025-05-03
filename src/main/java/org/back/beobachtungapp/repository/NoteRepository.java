package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface NoteRepository extends JpaRepository<Note, Long> {
  Optional<Note> findById(Long id);

  List<Note> findByChildId(Long childId);
}
