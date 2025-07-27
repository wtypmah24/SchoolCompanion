package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GoalDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(GoalRequestDto dto, Long childId) {
    String sql =
        """
                    INSERT INTO goals
                    (description, child_id)
                    VALUES (?, ?)
                """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(2, dto.description());
          ps.setLong(6, childId);
        });
  }

  public void update(GoalRequestDto dto, Long goalId) {
    String sql =
        """
                    UPDATE goals SET
                        description = COALESCE(?, description)
                    WHERE id = ?
                """;

    jdbcTemplate.update(sql, dto.description(), goalId);
  }

  public void delete(Long goalId) {
    String sql = "DELETE FROM goals WHERE id = ?";
    jdbcTemplate.update(sql, goalId);
  }

  public Optional<GoalResponseDto> findById(Long id) {
    String sql = "SELECT * FROM goals WHERE id = ?";
    List<GoalResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToGoal);

    return results.stream().findFirst();
  }

  public List<GoalResponseDto> findByChildId(Long childId) {
    String sql = "SELECT * FROM goals WHERE child_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToGoal);
  }

  public List<GoalResponseDto> findAll() {
    String sql = "SELECT * FROM goals";
    return jdbcTemplate.query(sql, this::mapRowToGoal);
  }

  private GoalResponseDto mapRowToGoal(ResultSet rs, int rowNum) throws SQLException {
    return new GoalResponseDto(rs.getLong("id"), rs.getString("description"));
  }
}
