package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.monitoring.MonitoringParamRequestDto;
import org.back.beobachtungapp.dto.response.monitoring.MonitoringParamResponseDto;
import org.back.beobachtungapp.dto.update.monitoring.MonitoringParamUpdateDto;
import org.back.beobachtungapp.entity.monitoring.ScaleType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParamDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(MonitoringParamRequestDto dto, Long companionId) {
    String sql =
        """
            INSERT INTO monitoring_parameters
            (title, type, description, min_value, max_value, companion_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.title());
          ps.setString(2, dto.type().name());
          ps.setString(3, dto.description());
          ps.setInt(4, dto.minValue());
          ps.setInt(5, dto.maxValue());
          ps.setLong(5, companionId);
        });
  }

  public void update(MonitoringParamUpdateDto dto, Long monitoringId) {
    String sql =
        """
            UPDATE monitoring_parameters SET
                title = COALESCE(?, title),
                type = COALESCE(?, type),
                description = COALESCE(?, description)
            WHERE id = ?
        """;

    jdbcTemplate.update(sql, dto.title(), dto.scaleType().name(), dto.description(), monitoringId);
  }

  public void delete(Long monitoringId) {
    String sql = "DELETE FROM monitoring_parameters WHERE id = ?";
    jdbcTemplate.update(sql, monitoringId);
  }

  public Optional<MonitoringParamResponseDto> findById(Long id) {
    String sql = "SELECT * FROM monitoring_parameters WHERE id = ?";
    List<MonitoringParamResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToParam);

    return results.stream().findFirst();
  }

  public List<MonitoringParamResponseDto> findByCompanionId(Long companionId) {
    String sql = "SELECT * FROM monitoring_parameters WHERE companion_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, companionId), this::mapRowToParam);
  }

  public List<MonitoringParamResponseDto> findAll() {
    String sql = "SELECT * FROM monitoring_parameters";
    return jdbcTemplate.query(sql, this::mapRowToParam);
  }

  private MonitoringParamResponseDto mapRowToParam(ResultSet rs, int rowNum) throws SQLException {
    return new MonitoringParamResponseDto(
        rs.getLong("id"),
        rs.getString("title"),
        ScaleType.valueOf(rs.getString("type")),
        rs.getString("description"),
        rs.getInt("min_value"),
        rs.getInt("max_value"),
        rs.getTimestamp("created_at").toInstant());
  }
}
