package org.back.beobachtungapp.repository;

import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.child.Child;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChildRepository extends JpaRepository<Child, Integer> {

  List<Child> findAllBySchoolCompanionId(Long companionId);

  @EntityGraph(attributePaths = {"specialNeeds", "goals", "notes"})
  @Query("SELECT c FROM Child c WHERE c.id = :id")
  Optional<Child> findByIdCustom(Long id);
}
