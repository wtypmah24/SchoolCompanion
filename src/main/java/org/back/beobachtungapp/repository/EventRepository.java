package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface EventRepository extends JpaRepository<Event, Long> {
  Optional<Event> findById(Long id);

  List<Event> findByCompanionId(Long companionId);

  List<Event> findEventsByChildId(Long childId);
}
