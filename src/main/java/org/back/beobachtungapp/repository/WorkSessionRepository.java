package org.back.beobachtungapp.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.session.WorkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

  Optional<WorkSession> findByCompanionIdAndStartTimeBetween(
      Long companyId, Instant startTime, Instant endTime);

  @Modifying
  @Transactional
  @Query(
      "UPDATE WorkSession ws SET ws.endTime = :now WHERE ws.startTime >= :startOfDay AND ws.startTime < :endOfDay AND ws.endTime IS NULL")
  void endAllSessionsStartedToday(Instant startOfDay, Instant endOfDay, Instant now);

  List<WorkSession> findByCompanionIdAndCreatedAtBetween(
      Long companionId, Instant startOfDay, Instant endOfDay);
}
