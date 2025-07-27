package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.dto.update.task.TaskUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TaskDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(TaskRequestDto dto, Long childId, Long companionId) {
    String sql =
        """
                    INSERT INTO tasks
                    (title, description, status, dead_line, child_id, companion_id)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.title());
          ps.setString(2, dto.description());
          ps.setString(3, dto.status());
          ps.setTimestamp(4, Timestamp.from(dto.deadLine()));
          ps.setLong(5, childId);
          ps.setLong(6, companionId);
        });
  }

  public void update(TaskUpdateDto dto, Long taskId) {
    String sql =
        """
                    UPDATE tasks SET
                        title = COALESCE(?, title),
                        description = COALESCE(?, description),
                        status = COALESCE(?, status),
                        dead_line = COALESCE(?, dead_line)
                    WHERE id = ?
                """;

    jdbcTemplate.update(sql, dto.title(), dto.description(), dto.status(), dto.deadLine(), taskId);
  }

  public void delete(Long taskId) {
    String sql = "DELETE FROM tasks WHERE id = ?";
    jdbcTemplate.update(sql, taskId);
  }

  public Optional<TaskResponseDto> findById(Long id) {
    String sql = "SELECT * FROM tasks WHERE id = ?";
    List<TaskResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToTask);

    return results.stream().findFirst();
  }

  public List<TaskResponseDto> findByChildId(Long childId) {
    String sql = "SELECT * FROM tasks WHERE child_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToTask);
  }

  public List<TaskResponseDto> findByCompanionId(Long companionId) {
    String sql = "SELECT * FROM tasks WHERE companion_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, companionId), this::mapRowToTask);
  }

  public List<TaskResponseDto> findAll() {
    String sql = "SELECT * FROM tasks";
    return jdbcTemplate.query(sql, this::mapRowToTask);
  }

  private TaskResponseDto mapRowToTask(ResultSet rs, int rowNum) throws SQLException {

    return new TaskResponseDto(
        rs.getLong("id"),
        rs.getString("type"),
        rs.getString("description"),
        rs.getString("status"),
        rs.getTimestamp("dead_line").toInstant());
  }
}
