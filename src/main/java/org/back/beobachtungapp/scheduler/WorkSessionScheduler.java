package org.back.beobachtungapp.scheduler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.dao.SessionDao;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.integration.telegram.TgBot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WorkSessionScheduler {
  private final SessionDao sessionDao;
  private final CompanionDao companionDao;
  private final TgBot tgBot;

  @Scheduled(cron = "@daily")
  @Transactional
  public void onSchedule() {
    Instant now = Instant.now();
    ZoneId zone = ZoneId.of("Europe/Berlin");

    LocalDate yesterday = LocalDate.now(zone).minusDays(1);
    Instant startOfDay = yesterday.atStartOfDay(zone).toInstant();

    sessionDao.endAllSessionsStartedToday(startOfDay, now);
  }

  @Scheduled(cron = "0 * * * * *")
  public void onScheduleHourly() {
    List<CompanionDto> companions = companionDao.getCompanionsByStartWorkingHors();
    String message = "Start work session";
    companions.forEach(
        companion -> {
          tgBot.sendMessage(companion.tgId(), message);
        });
  }
}
