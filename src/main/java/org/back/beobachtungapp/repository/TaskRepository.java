package org.back.beobachtungapp.repository;

import io.micrometer.common.lang.NonNullApi;
import java.util.List;
import org.back.beobachtungapp.entity.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

@NonNullApi
public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByCompanionId(Long companionId);

  List<Task> findEventsByChildId(Long childId);
}
