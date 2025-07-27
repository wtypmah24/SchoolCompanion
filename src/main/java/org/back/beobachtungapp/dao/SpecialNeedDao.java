package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SpecialNeedDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(SpecialNeedRequestDto dto, Long childId) {
    String sql = "INSERT INTO special_needs (type, description, child_id) VALUES (?, ?, ?)";
    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.type());
          ps.setString(2, dto.description());
          ps.setLong(3, childId);
        });
  }

  public void update(SpecialNeedUpdateDto dto, Long specialNeedId) {
    String sql =
        """
              UPDATE special_needs SET
                  type = COALESCE(?, type),
                  description = COALESCE(?, description)
              WHERE id = ?
          """;

    jdbcTemplate.update(sql, dto.type(), dto.description(), specialNeedId);
  }

  public void delete(Long specialNeedId) {
    String sql = "DELETE FROM special_needs WHERE id = ?";
    jdbcTemplate.update(sql, specialNeedId);
  }

  public Optional<SpecialNeedResponseDto> findById(Long specialNeedId) {
    String sql = "SELECT * FROM special_needs WHERE id = ?";

    List<SpecialNeedResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, specialNeedId), this::mapRowToSpecialNeed);

    return results.stream().findFirst();
  }

  public List<SpecialNeedResponseDto> findAll() {
    String sql = "SELECT * FROM special_needs";
    return jdbcTemplate.query(sql, this::mapRowToSpecialNeed);
  }

  public List<SpecialNeedResponseDto> findByChildId(Long childId) {
    String sql = "SELECT * FROM special_needs WHERE child_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToSpecialNeed);
  }

  private SpecialNeedResponseDto mapRowToSpecialNeed(ResultSet rs, int rowNum) throws SQLException {
    return new SpecialNeedResponseDto(
        rs.getLong("id"), rs.getString("type"), rs.getString("description"));
  }
}
