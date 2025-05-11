package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.request.task.TaskUpdateDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.entity.task.Task;
import org.back.beobachtungapp.event.CacheEvent;
import org.back.beobachtungapp.mapper.TaskMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.TaskRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TaskService {
  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  private final ChildRepository childRepository;
  private final ApplicationEventPublisher eventPublisher;

  public TaskService(
      TaskRepository taskRepository,
      TaskMapper taskMapper,
      ChildRepository childRepository,
      ApplicationEventPublisher eventPublisher) {
    this.taskRepository = taskRepository;
    this.taskMapper = taskMapper;
    this.childRepository = childRepository;
    this.eventPublisher = eventPublisher;
  }

  public TaskResponseDto save(TaskRequestDto dto, CompanionDto companionDto, Long childId) {
    log.info(
        "Saving new task for child with id: {} and companion with id: {}",
        childId,
        companionDto.id());
    Task task = taskMapper.taskRequestDtoToTask(dto);
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
    child.addTask(task);
    Companion companionRef = new Companion();
    companionRef.setId(companionDto.id());
    task.setCompanion(companionRef);
    Task savedTask = taskRepository.save(task);
    return taskMapper.taskToTaskResponseDto(savedTask);
  }

  @Transactional
  public TaskResponseDto update(TaskUpdateDto taskUpdateDto, Long taskId) {
    log.info("Updating task with id: {}", taskId);

    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(
                () -> {
                  log.error("Task not found with id: {}", taskId);
                  return new EntityNotFoundException("Task not found with id: " + taskId);
                });

    taskMapper.updateTask(taskUpdateDto, task);
    log.info("Event with id: {} successfully updated", taskId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"task", "tasks"}, new Object[] {taskId, task.getCompanion().getId()}));

    return taskMapper.taskToTaskResponseDto(task);
  }

  @Transactional
  public void delete(Long taskId) {
    log.info("Deleting task with id: {}", taskId);

    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(
                () -> {
                  log.error("Task not found with id: {}", taskId);
                  return new EntityNotFoundException("Task not found with id: " + taskId);
                });

    taskRepository.delete(task);
    log.info("Task with id: {} successfully deleted", taskId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"task", "tasks"}, new Object[] {taskId, task.getCompanion().getId()}));
  }

  @Cacheable(value = "tasks", key = "#companionDto.id()", unless = "#result.isEmpty()")
  public List<TaskResponseDto> findAll(CompanionDto companionDto) {
    log.info("Fetching all tasks for companion with id: {}", companionDto.id());

    List<TaskResponseDto> events =
        taskMapper.taskToTaskResponseDtoList(taskRepository.findByCompanionId(companionDto.id()));

    log.info("Found {} tasks for companion with id: {}", events.size(), companionDto.id());

    return events;
  }

  public List<TaskResponseDto> findByChildId(Long childId) {
    log.info("Fetching all tasks for child with id: {}", childId);

    List<TaskResponseDto> events =
        taskMapper.taskToTaskResponseDtoList(taskRepository.findEventsByChildId(childId));

    log.info("Found {} tasks for child with id: {}", events.size(), childId);

    return events;
  }

  @Cacheable(value = "task", key = "#taskId", unless = "#result == null")
  public TaskResponseDto findById(Long taskId) {
    log.info("Fetching task with id: {}", taskId);

    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(
                () -> {
                  log.error("ask not found with id: {}", taskId);
                  return new EntityNotFoundException("Task not found with id: " + taskId);
                });

    log.info("Found event with id: {}", taskId);
    return taskMapper.taskToTaskResponseDto(task);
  }
}
