package org.back.beobachtungapp.repository;

import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
  Optional<Goal> findById(Long id);

  List<Goal> findByChildId(Long childId);
}
