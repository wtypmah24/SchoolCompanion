package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.note.NoteRequestDto;
import org.back.beobachtungapp.dto.response.note.NoteResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoteDao {

  private final JdbcTemplate jdbcTemplate;

  public void save(NoteRequestDto dto, Long childId) {
    String sql =
        """
            INSERT INTO notes
            (content, child_id)
            VALUES (?, ?)
        """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.content());
          ps.setLong(2, childId);
        });
  }

  public void update(NoteRequestDto dto, Long noteId) {
    String sql =
        """
            UPDATE notes SET
                content = COALESCE(?, content)
            WHERE id = ?
        """;

    jdbcTemplate.update(sql, dto.content(), noteId);
  }

  public void delete(Long noteId) {
    String sql = "DELETE FROM notes WHERE id = ?";
    jdbcTemplate.update(sql, noteId);
  }

  public Optional<NoteResponseDto> findById(Long id) {
    String sql = "SELECT * FROM notes WHERE id = ?";
    List<NoteResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToNote);

    return results.stream().findFirst();
  }

  public List<NoteResponseDto> findByChildId(Long childId) {
    String sql = "SELECT * FROM notes WHERE child_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, childId), this::mapRowToNote);
  }

  public List<NoteResponseDto> findAll() {
    String sql = "SELECT * FROM notes";
    return jdbcTemplate.query(sql, this::mapRowToNote);
  }

  private NoteResponseDto mapRowToNote(ResultSet rs, int rowNum) throws SQLException {
    return new NoteResponseDto(rs.getLong("id"), rs.getString("content"));
  }
}
