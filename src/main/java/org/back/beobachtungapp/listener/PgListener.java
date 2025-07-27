package org.back.beobachtungapp.listener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgListener {
  private Connection conn;
  private ScheduledExecutorService scheduler;
  private final DataSource dataSource;
  private final NotificationHandler notificationHandler;

  @PostConstruct
  public void startListener() {
    try {
      log.info("▶️ Starting PostgreSQL LISTEN listener...");
      conn =
          DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres", "cat", "secret");
      conn.setAutoCommit(true); // Обязательно!

      Statement stmt = conn.createStatement();
      PGConnection pgConnection = conn.unwrap(PGConnection.class);
      stmt.execute("LISTEN event_channel");
      log.info("🟢 LISTEN event_channel successfully set.");
      scheduler =
          Executors.newSingleThreadScheduledExecutor(
              new PgListener.NamedThreadFactory("pg-listener"));
      scheduler.scheduleAtFixedRate(
          () -> checkNotifications(pgConnection), 0, 500, TimeUnit.MILLISECONDS);

    } catch (Exception e) {
      log.error("Failed to initialize listener", e);
    }
  }

  private void checkNotifications(PGConnection pgConnection) {
    try {
      PGNotification[] notifications = pgConnection.getNotifications();
      if (notifications != null) {
        for (PGNotification notification : notifications) {
          log.info("📨 Received notification: {}", notification.getParameter());
          notificationHandler.handleNotification(notification.getParameter());
        }
      }
    } catch (SQLException e) {
      log.error("Failed to retrieve PostgreSQL notifications", e);
    }
  }

  @PreDestroy
  public void shutdown() {
    if (scheduler != null) scheduler.shutdown();
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        log.warn("Failed to close PG listener connection", e);
      }
    }
  }

  private static class NamedThreadFactory implements ThreadFactory {
    private final String baseName;
    private final AtomicInteger counter = new AtomicInteger(1);

    public NamedThreadFactory(String baseName) {
      this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r, baseName + "-" + counter.getAndIncrement());
      t.setDaemon(true);
      return t;
    }
  }
}
