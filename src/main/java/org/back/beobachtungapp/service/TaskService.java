package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.TaskDao;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.dto.update.task.TaskUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskDao taskDao;

  public void save(TaskRequestDto dto, CompanionDto companionDto, Long childId) {
    taskDao.save(dto, childId, companionDto.id());
  }

  @CacheEvict(value = "task", key = "#taskId")
  @Transactional
  public void update(TaskUpdateDto taskUpdateDto, Long taskId) {
    taskDao.update(taskUpdateDto, taskId);
  }

  @CacheEvict(value = "task", key = "#taskId")
  @Transactional
  public void delete(Long taskId) {
    taskDao.delete(taskId);
  }

  public List<TaskResponseDto> findAll(CompanionDto companionDto) {
    return taskDao.findByCompanionId(companionDto.id());
  }

  public List<TaskResponseDto> findByChildId(Long childId) {
    return taskDao.findByChildId(childId);
  }

  @Cacheable(value = "task", key = "#taskId", unless = "#result == null")
  public TaskResponseDto findById(Long taskId) {
    return taskDao
        .findById(taskId)
        .orElseThrow(
            () -> {
              log.error("Task not found with id: {}", taskId);
              return new BadRequestException("Task not found with id: " + taskId);
            });
  }
}
