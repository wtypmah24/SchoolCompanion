package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface GoalRepository extends JpaRepository<Goal, Long> {
  Optional<Goal> findById(Long id);

  List<Goal> findByChildId(Long childId);
}
