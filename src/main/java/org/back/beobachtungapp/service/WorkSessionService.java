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
    sessionDao.end(companionDto.id(), Instant.now(), startOfDay());
  }

  public WorkSessionResponseDto isWorking(CompanionDto companionDto) {
    return sessionDao.findTodayWorkSession(companionDto.id(), startOfDay()).orElse(null);
  }

  public List<WorkSessionResponseDto> getWorkSessionsByDates(
      CompanionDto companionDto, LocalDate start, LocalDate end) {
    ZoneId zone = ZoneId.of("Europe/Berlin");

    Instant startInstant = start.atStartOfDay(zone).toInstant();
    Instant endInstant = end.plusDays(1).atStartOfDay(zone).minusNanos(1).toInstant();

    return sessionDao.findSessionsByDateRange(companionDto.id(), startInstant, endInstant);
  }

  private boolean hasWorkSessionToday(Long companionId) {
    return sessionDao.findTodayWorkSession(companionId, startOfDay()).isPresent();
  }

  private Instant startOfDay() {
    return ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
        .toLocalDate()
        .atStartOfDay(ZoneId.of("Europe/Berlin"))
        .toInstant();
  }

  private Instant endOfDay() {
    return ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
        .toLocalDate()
        .plusDays(1)
        .atStartOfDay(ZoneId.of("Europe/Berlin"))
        .minusNanos(1)
        .toInstant();
  }
}
