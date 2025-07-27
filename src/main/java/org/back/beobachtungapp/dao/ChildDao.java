package org.back.beobachtungapp.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.update.child.ChildUpdateDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChildDao {

  private final JdbcTemplate jdbcTemplate;

  public void save(ChildRequestDto childDto, Long companionId) {
    String sql =
        """
        INSERT INTO children (name, surname, email, phone_number, date_of_birth, companion_id)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, childDto.name());
          ps.setString(2, childDto.surname());
          ps.setString(3, childDto.email());
          ps.setString(4, childDto.phoneNumber());
          ps.setDate(5, Date.valueOf(childDto.dateOfBirth()));
          ps.setLong(6, companionId);
        });
  }

  public void update(ChildUpdateDto dto, Long childId) {
    String sql =
        """
                    UPDATE children SET
                        name = COALESCE(?, name),
                        surname = COALESCE(?, surname),
                        email = COALESCE(?, email),
                        phone_number = COALESCE(?, phone_number),
                        date_of_birth = COALESCE(?, date_of_birth)
                    WHERE id = ?
                """;

    jdbcTemplate.update(
        sql, dto.name(), dto.surname(), dto.email(), dto.phoneNumber(), dto.dateOfBirth(), childId);
  }

  public void delete(Long childId) {
    String sql = "DELETE FROM children WHERE id = ?";
    jdbcTemplate.update(sql, childId);
  }

  public List<ChildResponseDto> findAllBySchoolCompanionId(Long companionId) {
    String sql = "SELECT * FROM children WHERE companion_id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, companionId), this::mapRowToChild);
  }

  public Optional<ChildResponseDto> findById(Long id) {
    String sql = "SELECT * FROM children WHERE id = ?";

    List<ChildResponseDto> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToChild);

    return results.stream().findFirst();
  }

  private ChildResponseDto mapRowToChild(ResultSet rs, int rowNum) throws SQLException {
    return new ChildResponseDto(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("surname"),
        rs.getString("email"),
        rs.getString("phone_number"),
        rs.getDate("date_of_birth").toLocalDate(),
        rs.getBoolean("active"));
  }
}
