package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.event.EventRequestDto;
import org.back.beobachtungapp.dto.response.event.EventResponseDto;
import org.back.beobachtungapp.dto.update.event.EventUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EventDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(EventRequestDto dto, Long childId, Long companionId) {
    String sql =
        """
        INSERT INTO events
        (title, description, start_date_time, end_date_time, location, child_id, companion_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.title());
          ps.setString(2, dto.description());
          ps.setTimestamp(3, Timestamp.from(dto.startDateTime()));
          ps.setTimestamp(4, Timestamp.from(dto.endDateTime()));
          ps.setString(5, dto.location());
          ps.setLong(6, childId);
          ps.setLong(7, companionId);
        });
  }

  public void update(EventUpdateDto dto, Long eventId) {
    String sql =
        """
            UPDATE events SET
                title = COALESCE(?, title),
                description = COALESCE(?, description),
                start_date_time = COALESCE(?, start_date_time),
                end_date_time = COALESCE(?, end_date_time),
                location = COALESCE(?, location)
            WHERE id = ?
        """;

    jdbcTemplate.update(
        sql,
        dto.title(),
        dto.description(),
        dto.startDateTime(),
        dto.endDateTime(),
        dto.location(),
        eventId);
  }

  public void delete(Long eventId) {
    String sql = "DELETE FROM events WHERE id = ?";
    jdbcTemplate.update(sql, eventId);
  }

  public Optional<EventResponseDto> findById(Long id) {
    String sql = "SELECT * FROM events WHERE id = ?";
    List<EventResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToEvent);

    return results.stream().findFirst();
  }

  public List<EventResponseDto> findByChildId(Long childId) {
    String sql = "SELECT * FROM events WHERE child_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToEvent);
  }

  public List<EventResponseDto> findByCompanionId(Long companionId) {
    String sql = "SELECT * FROM events WHERE companion_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, companionId), this::mapRowToEvent);
  }

  private EventResponseDto mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
    return new EventResponseDto(
        rs.getLong("id"),
        rs.getString("title"),
        rs.getString("description"),
        rs.getTimestamp("start_date_time").toInstant(),
        rs.getTimestamp("end_date_time").toInstant(),
        rs.getString("location"),
        rs.getLong("child_id"));
  }
}
