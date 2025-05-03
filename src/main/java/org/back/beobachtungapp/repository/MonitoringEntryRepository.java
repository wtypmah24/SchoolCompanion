package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.monitoring.MonitoringEntry;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface MonitoringEntryRepository extends JpaRepository<MonitoringEntry, Long> {
  Optional<MonitoringEntry> findById(Long id);

  List<MonitoringEntry> findByChildId(Long childId);
}
