package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface SpecialNeedRepository extends JpaRepository<SpecialNeed, Long> {
  Optional<SpecialNeed> findById(Long id);

  List<SpecialNeed> findByChildId(Long childId);
}
