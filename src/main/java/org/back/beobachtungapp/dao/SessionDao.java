package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.response.session.WorkSessionResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SessionDao {
  private final JdbcTemplate jdbcTemplate;

  public void start(Instant start, Long companionId) {
    String sql =
        """
                    INSERT INTO work_sessions
                    (start_time, companion_id)
                    VALUES (?, ?)
                """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setTimestamp(1, Timestamp.from(start));
          ps.setLong(2, companionId);
        });
  }

  public void end(Long companionId, Instant now, Instant todayStart) {
    String sql =
        "UPDATE work_sessions SET end_time = ? WHERE companion_id = ? AND end_time IS NULL AND start_time >= ?";

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setTimestamp(1, new Timestamp(now.toEpochMilli()));
          ps.setLong(2, companionId);
          ps.setTimestamp(3, new Timestamp(todayStart.toEpochMilli()));
        });
  }

  public Optional<WorkSessionResponseDto> findTodayWorkSession(
      Long companionId, Instant todayStart) {
    String sql =
        """
            SELECT * FROM work_sessions
            WHERE companion_id = ?
              AND end_time IS NULL
              AND start_time >= ?
        """;

    return jdbcTemplate
        .query(
            sql,
            ps -> {
              ps.setLong(1, companionId);
              ps.setTimestamp(2, Timestamp.from(todayStart));
            },
            this::mapRowToSession)
        .stream()
        .findFirst();
  }

  public List<WorkSessionResponseDto> findSessionsByDateRange(
      Long companionId, Instant start, Instant end) {
    String sql =
        """
              SELECT * FROM work_sessions
              WHERE companion_id = ?
                  AND created_at >= ?
                  AND created_at <= ?
          """;

    return jdbcTemplate.query(
        sql,
        ps -> {
          ps.setLong(1, companionId);
          ps.setTimestamp(2, Timestamp.from(start));
          ps.setTimestamp(3, Timestamp.from(end));
        },
        this::mapRowToSession);
  }

  public void endAllSessionsStartedToday(Instant start, Instant now) {
    String sql =
        """
                  UPDATE work_sessions
                  SET end_time = ?
                    WHERE end_time IS NULL
                    AND start_time >= ?
              """;
    jdbcTemplate.update(sql, now, start);
  }

  private WorkSessionResponseDto mapRowToSession(ResultSet rs, int rowNum) throws SQLException {
    Timestamp startTimestamp = rs.getTimestamp("start_time");
    Timestamp endTimestamp = rs.getTimestamp("end_time");

    return new WorkSessionResponseDto(
        rs.getLong("id"),
        startTimestamp != null ? startTimestamp.toInstant() : null,
        endTimestamp != null ? endTimestamp.toInstant() : null,
        rs.getString("note"));
  }
}
