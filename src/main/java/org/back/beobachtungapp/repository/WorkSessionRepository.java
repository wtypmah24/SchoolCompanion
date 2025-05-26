package org.back.beobachtungapp.repository;

import java.time.Instant;
import java.util.Optional;
import org.back.beobachtungapp.entity.session.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

  Optional<WorkSession> findByCompanionIdAndStartTimeBetween(
      Long companyId, Instant startTime, Instant endTime);
}
