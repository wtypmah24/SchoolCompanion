package org.back.beobachtungapp.repository;

import java.util.Optional;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface CompanionRepository extends JpaRepository<Companion, Long> {

  @NonNull
  Optional<Companion> findById(@NonNull Long id);

  Optional<Companion> findByEmail(String email);

  @Modifying
  @Query(
      value =
          "DELETE FROM companion_thread_ids WHERE companion_id = :companionId AND thread_id = :threadId",
      nativeQuery = true)
  void removeThread(@Param("companionId") Long companionId, @Param("threadId") String threadId);
}
