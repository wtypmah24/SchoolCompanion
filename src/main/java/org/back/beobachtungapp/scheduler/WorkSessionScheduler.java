package org.back.beobachtungapp.scheduler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dao.SessionDao;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WorkSessionScheduler {
  private final SessionDao sessionDao;

  @Scheduled(cron = "@daily")
  @Transactional
  public void onSchedule() {
    Instant now = Instant.now();
    ZoneId zone = ZoneOffset.UTC; // TODO: change to LT

    LocalDate yesterday = LocalDate.now(zone).minusDays(1);
    Instant startOfDay = yesterday.atStartOfDay(zone).toInstant();

    sessionDao.endAllSessionsStartedToday(startOfDay, now);
  }
}
