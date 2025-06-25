package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.dto.update.task.TaskUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.task.Task;
import org.back.beobachtungapp.mapper.TaskMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.back.beobachtungapp.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  private final ChildRepository childRepository;
  private final CompanionRepository companionRepository;

  public TaskResponseDto save(TaskRequestDto dto, CompanionDto companionDto, Long childId) {
    Task task = taskMapper.taskRequestDtoToTask(dto);
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
    child.addTask(task);
    Companion companion = companionRepository.getReferenceById(companionDto.id());
    task.setCompanion(companion);
    Task savedTask = taskRepository.save(task);
    return taskMapper.taskToTaskResponseDto(savedTask);
  }

  @CacheEvict(value = "task", key = "#taskId")
  @Transactional
  public TaskResponseDto update(TaskUpdateDto taskUpdateDto, Long taskId) {
    Task task = findTaskOrThrow(taskId);
    taskMapper.updateTask(taskUpdateDto, task);
    return taskMapper.taskToTaskResponseDto(task);
  }

  @CacheEvict(value = "task", key = "#taskId")
  @Transactional
  public void delete(Long taskId) {
    Task task = findTaskOrThrow(taskId);
    taskRepository.delete(task);
  }

  public List<TaskResponseDto> findAll(CompanionDto companionDto) {
    return taskMapper.taskToTaskResponseDtoList(
        taskRepository.findByCompanionId(companionDto.id()));
  }

  public List<TaskResponseDto> findByChildId(Long childId) {
    return taskMapper.taskToTaskResponseDtoList(taskRepository.findEventsByChildId(childId));
  }

  @Cacheable(value = "task", key = "#taskId", unless = "#result == null")
  public TaskResponseDto findById(Long taskId) {
    Task task = findTaskOrThrow(taskId);
    return taskMapper.taskToTaskResponseDto(task);
  }

  private Task findTaskOrThrow(Long taskId) {
    return taskRepository
        .findById(taskId)
        .orElseThrow(
            () -> {
              log.error("Task not found with id: {}", taskId);
              return new EntityNotFoundException("Task not found with id: " + taskId);
            });
  }
}
