package org.back.beobachtungapp.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.dto.request.companion.CompanionAdTgIdDto;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanionDao {
  private final JdbcTemplate jdbcTemplate;

  public void save(CompanionRequestDto dto) {
    String sql =
        """
                        INSERT INTO companions (name, surname, email, password, organization)
                        VALUES (?, ?, ?, ?, ?)
                    """;

    jdbcTemplate.update(
        sql,
        ps -> {
          ps.setString(1, dto.name());
          ps.setString(2, dto.surname());
          ps.setString(3, dto.email());
          ps.setString(4, dto.password());
          ps.setString(5, dto.organization());
        });
  }

  public CompanionDto update(CompanionUpdateDto dto, Long companionId) {
    String sql =
        """
          UPDATE companions SET
            name = COALESCE(?, name),
            surname = COALESCE(?, surname),
            email = COALESCE(?, email),
            organization = COALESCE(?, organization),
            workday_start = COALESCE(?, workday_start),
            workday_end = COALESCE(?, workday_end)
          WHERE id = ?
          RETURNING *
      """;

    return jdbcTemplate.queryForObject(
        sql,
        this::mapRowToCompanionDto,
        dto.name(),
        dto.surname(),
        dto.email(),
        dto.organization(),
        dto.startWorkingTime(),
        dto.endWorkingTime(),
        companionId);
  }

  public void delete(Long companionId) {
    String sql = "DELETE FROM companions WHERE id = ?";
    jdbcTemplate.update(sql, companionId);
  }

  public Optional<Companion> findById(Long id) {
    String sql = "SELECT * FROM companions WHERE id = ?";

    List<Companion> results =
        jdbcTemplate.query(sql, ps -> ps.setLong(1, id), this::mapRowToCompanion);

    return results.stream().findFirst();
  }

  public Optional<CompanionDto> findByEmail(String email) {
    String sql = "SELECT * FROM companions WHERE email = ?";

    List<CompanionDto> results =
        jdbcTemplate.query(sql, ps -> ps.setString(1, email), this::mapRowToCompanionDto);

    return results.stream().findFirst();
  }

  public Optional<Companion> findCompanionByEmail(String email) {
    String sql = "SELECT * FROM companions WHERE email = ?";

    List<Companion> results =
        jdbcTemplate.query(sql, ps -> ps.setString(1, email), this::mapRowToCompanion);

    return results.stream().findFirst();
  }

  public void removeThread(Long companionId, String threadId) {
    String sql = "DELETE FROM companion_thread_ids WHERE companion_id = ? AND thread_id = ?";
    jdbcTemplate.update(sql, companionId, threadId);
  }

  public void addThreadToCompanion(Long companionId, String threadId) {
    String sql = "INSERT INTO companion_thread_ids (companion_id, thread_id) VALUES (?, ?)";
    jdbcTemplate.update(sql, companionId, threadId);
  }

  public void updatePassword(Long companionId, String newPassword, String oldPassword) {
    String sql = "UPDATE companions SET password = ? WHERE id = ? AND password = ?";
    jdbcTemplate.update(sql, newPassword, companionId, oldPassword);
  }

  public void addTgId(CompanionAdTgIdDto tgDto) {
    String sql = "UPDATE companions SET tg_id = ? WHERE email = ?";
    jdbcTemplate.update(sql, tgDto.tgId(), tgDto.email());
  }

  public List<String> getChatIds(Long companionId) {
    String sql = "SELECT thread_id FROM companion_thread_ids WHERE companion_id = ?";
    return jdbcTemplate.query(
        sql, ps -> ps.setLong(1, companionId), (rs, rowNum) -> rs.getString("thread_id"));
  }

  public void addAvatarRefToCompanion(Long companionId, String avatarId) {
    String sql = "UPDATE companions SET avatar_id = ? WHERE id = ?";
    jdbcTemplate.update(sql, avatarId, companionId);
  }

  public void removeAvatarRefFromCompanion(Long companionId) {
    String sql = "UPDATE companions SET avatar_id = NULL WHERE id = ?";
    jdbcTemplate.update(sql, companionId);
  }

  public boolean getNotificationStatus(Long companionId) {
    String sql = "SELECT notification FROM companions WHERE id = ?";
    return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, companionId));
  }

  public void updateNotificationStatus(Long companionId, boolean status) {
    String sql = "UPDATE companions SET notification = ? WHERE id = ?";
    jdbcTemplate.update(sql, status, companionId);
  }

  public List<CompanionDto> getCompanionsByStartWorkingHors() {
    String sql =
        """
            SELECT * FROM companions
            WHERE workday_start >= ? AND workday_start < ?
              AND notification = true AND tg_id IS NOT NULL
        """;

    LocalTime currentTime = LocalTime.now();
    LocalTime oneMinuteLater = currentTime.plusMinutes(1);

    return jdbcTemplate.query(
        sql,
        ps -> {
          ps.setTime(1, java.sql.Time.valueOf(currentTime));
          ps.setTime(2, java.sql.Time.valueOf(oneMinuteLater));
        },
        this::mapRowToCompanionDto);
  }

  private CompanionDto mapRowToCompanionDto(ResultSet rs, int rowNum) throws SQLException {
    return new CompanionDto(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("surname"),
        rs.getString("organization"),
        rs.getString("email"),
        rs.getString("tg_id"),
        rs.getString("avatar_id"),
        rs.getString("workday_start"),
        rs.getString("workday_end"),
        rs.getTimestamp("created_at").toInstant());
  }

  private Companion mapRowToCompanion(ResultSet rs, int rowNum) throws SQLException {
    Companion companion = new Companion();
    companion.setId(rs.getLong("id"));
    companion.setName(rs.getString("name"));
    companion.setSurname(rs.getString("surname"));
    companion.setEmail(rs.getString("email"));
    companion.setPassword(rs.getString("password"));
    companion.setOrganization(rs.getString("organization"));
    companion.setTgId(rs.getString("tg_id"));
    companion.setCreatedAt(rs.getTimestamp("created_at").toInstant());
    return companion;
  }
}
