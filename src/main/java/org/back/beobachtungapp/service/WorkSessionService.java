package org.back.beobachtungapp.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.session.WorkSession;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.back.beobachtungapp.repository.WorkSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSessionService {

  private final WorkSessionRepository workSessionRepository;
  private final CompanionRepository companionRepository;

  @Transactional
  public void startWorkSession(CompanionDto companionDto) {
    if (hasWorkSessionToday(companionDto.id())) {
      throw new IllegalStateException(
          "Work session already exists for companion id " + companionDto.id() + " today.");
    }

    Companion companion = companionRepository.getReferenceById(companionDto.id());
    WorkSession newWorkSession = new WorkSession();
    newWorkSession.setStartTime(currentInstant());
    newWorkSession.setCompanion(companion);

    workSessionRepository.save(newWorkSession);
  }

  @Transactional
  public void endWorkSession(CompanionDto companionDto) {
    WorkSession existingSession =
        findTodayWorkSession(companionDto.id())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Work session doesn't exist for companion id: " + companionDto.id()));

    existingSession.setEndTime(currentInstant());
    workSessionRepository.save(existingSession);
  }

  private boolean hasWorkSessionToday(Long companionId) {
    return findTodayWorkSession(companionId).isPresent();
  }

  private Optional<WorkSession> findTodayWorkSession(Long companionId) {
    Instant startOfDay = startOfUtcDay();
    Instant endOfDay = endOfUtcDay();
    return workSessionRepository.findByCompanionIdAndStartTimeBetween(
        companionId, startOfDay, endOfDay);
  }

  private Instant startOfUtcDay() {
    return ZonedDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
  }

  private Instant endOfUtcDay() {
    return ZonedDateTime.now(ZoneOffset.UTC)
        .toLocalDate()
        .plusDays(1)
        .atStartOfDay(ZoneOffset.UTC)
        .minusNanos(1)
        .toInstant();
  }

  private Instant currentInstant() {
    return Instant.now();
  }
}
