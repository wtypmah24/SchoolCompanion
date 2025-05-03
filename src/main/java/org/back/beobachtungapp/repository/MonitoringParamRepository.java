package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import java.util.Optional;
import org.back.beobachtungapp.entity.monitoring.MonitoringParameter;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface MonitoringParamRepository extends JpaRepository<MonitoringParameter, Long> {
  Optional<MonitoringParameter> findById(Long id);

  List<MonitoringParameter> findByCompanionId(Long companionId);
}
