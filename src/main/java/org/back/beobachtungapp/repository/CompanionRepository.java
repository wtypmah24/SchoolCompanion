package org.back.beobachtungapp.repository;

import org.back.beobachtungapp.entity.companion.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;


import java.util.Optional;

public interface CompanionRepository extends JpaRepository<Companion, Long> {

    @NonNull
    Optional<Companion> findById(@NonNull Long id);

    Optional<Companion> findByEmail(String email);

}
