package org.back.beobachtungapp.service;

import java.time.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.SessionDao;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.session.WorkSessionResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkSessionService {

  private final SessionDao sessionDao;

  @Transactional
  public void startWorkSession(CompanionDto companionDto) {
    if (hasWorkSessionToday(companionDto.id())) {
      throw new IllegalStateException(
          "Work session already exists for companion id " + companionDto.id() + " today.");
    }
    sessionDao.start(Instant.now(), companionDto.id());
  }

  @Transactional
  public void endWorkSession(CompanionDto companionDto) {
    sessionDao.end(companionDto.id(), Instant.now(), startOfUtcDay());
  }

  public WorkSessionResponseDto isWorking(CompanionDto companionDto) {
    return sessionDao.findTodayWorkSession(companionDto.id(), startOfUtcDay()).orElse(null);
  }

  public List<WorkSessionResponseDto> getWorkSessionsByDates(
      CompanionDto companionDto, LocalDate start, LocalDate end) {
    return sessionDao.findSessionsByDateRange(companionDto.id(), startOfUtcDay(), endOfUtcDay());
  }

  private boolean hasWorkSessionToday(Long companionId) {
    return sessionDao.findTodayWorkSession(companionId, startOfUtcDay()).isPresent();
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
}
