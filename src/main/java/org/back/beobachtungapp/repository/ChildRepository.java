package org.back.beobachtungapp.repository;

import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.Child;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child, Integer> {

  List<Child> findAllBySchoolCompanionId(Long companionId);

  @EntityGraph(attributePaths = {"specialNeeds", "goals", "notes"})
  Optional<Child> findById(Long id);
}
