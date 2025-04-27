package org.back.beobachtungapp.repository;

import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Integer> {

    List<Child> findAllBySchoolCompanion(Companion companion);
}

