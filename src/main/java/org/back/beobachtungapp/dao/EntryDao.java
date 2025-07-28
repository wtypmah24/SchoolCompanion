package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringEntryRequestDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringEntryResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringEntryUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class EntryDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(MonitoringEntryRequestDto dto, Long childId, Long monitoringParameterId) {
    String sql =
        """
                        INSERT INTO monitoring_entries
                        (value, notes, monitoring_parameter_id,  child_id)
                        VALUES (?, ?, ?, ?)
                    """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.value());
          ps.setString(2, dto.notes());
          ps.setLong(3, monitoringParameterId);
          ps.setLong(4, childId);
        });
  }

  public void update(MonitoringEntryUpdateDto dto, Long entryId) {
    String sql =
        """
                    UPDATE monitoring_entries SET
                        value = COALESCE(?, value),
                        notes = COALESCE(?, notes)
                    WHERE id = ?
                """;

    jdbcTemplate.update(sql, dto.value(), dto.notes(), entryId);
  }

  public void delete(Long entryId) {
    String sql = "DELETE FROM monitoring_entries WHERE id = ?";
    jdbcTemplate.update(sql, entryId);
  }

  public Optional<MonitoringEntryResponseDto> findById(Long id) {
    String sql =
        """
          SELECT me.id, me.value, me.notes, me.monitoring_parameter_id, me.child_id, me.created_at,
                 mp.title, mp.type
          FROM monitoring_entries me
          JOIN monitoring_parameters mp ON me.monitoring_parameter_id = mp.id
          WHERE me.id = ?
      """;
    List<MonitoringEntryResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToEntry);

    return results.stream().findFirst();
  }

  public List<MonitoringEntryResponseDto> findByChildId(Long childId) {
    String sql =
        """
            SELECT me.id, me.value, me.notes, me.monitoring_parameter_id, me.child_id, me.created_at,
                   mp.title, mp.type
            FROM monitoring_entries me
            JOIN monitoring_parameters mp ON me.monitoring_parameter_id = mp.id
            WHERE me.child_id = ?
        """;
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToEntry);
  }

  public List<MonitoringEntryResponseDto> findAll(long companionId) {
    String sql =
        """
                    SELECT me.id, me.value, me.notes, me.monitoring_parameter_id, me.child_id, me.created_at,
                           mp.title, mp.type
                    FROM monitoring_entries me
                    JOIN monitoring_parameters mp ON me.monitoring_parameter_id = mp.id
                    WHERE mp.companion_id = ?
                """;

    return jdbcTemplate.query(sql, ps -> ps.setLong(1, companionId), this::mapRowToEntry);
  }

  private MonitoringEntryResponseDto mapRowToEntry(ResultSet rs, int rowNum) throws SQLException {

    return new MonitoringEntryResponseDto(
        rs.getLong("id"),
        rs.getString("value"),
        rs.getString("notes"),
        rs.getLong("monitoring_parameter_id"),
        rs.getString("title"),
        rs.getString("type"),
        rs.getLong("child_id"),
        rs.getString("created_at"));
  }
}
