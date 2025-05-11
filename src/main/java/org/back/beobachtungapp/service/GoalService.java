package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.child.Goal;
import org.back.beobachtungapp.event.CacheEvent;
import org.back.beobachtungapp.mapper.GoalMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GoalService {
  private final GoalRepository goalRepository;
  private final GoalMapper goalMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final ChildRepository childRepository;

  @Autowired
  public GoalService(
      GoalRepository goalRepository,
      GoalMapper goalMapper,
      ApplicationEventPublisher eventPublisher,
      ChildRepository childRepository) {
    this.goalRepository = goalRepository;
    this.goalMapper = goalMapper;
    this.eventPublisher = eventPublisher;
    this.childRepository = childRepository;
  }

  @Transactional
  public GoalResponseDto save(GoalRequestDto goalDto, Long childId) {
    log.info("Saving new goal for child with id: {}", childId);

    Goal goal = goalMapper.goalRequestDtoToGoal(goalDto);

    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
    child.addGoal(goal);
    Goal savedGoal = goalRepository.save(goal);
    log.info(
        "Successfully saved goal with id: {} for child with id: {}", savedGoal.getId(), childId);

    return goalMapper.goalToGoalResponseDto(savedGoal);
  }

  @Transactional
  public GoalResponseDto update(GoalRequestDto goalDto, Long goalId) {
    log.info("Updating goal with id: {}", goalId);

    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(
                () -> {
                  log.error("Goal not found with id: {}", goalId);
                  return new EntityNotFoundException("Goal not found with id: " + goalId);
                });

    goalMapper.updateGoalFromDto(goalDto, goal);
    log.info("Goal with id: {} successfully updated", goalId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"goal", "goals"}, new Object[] {goalId, goal.getChild().getId()}));

    return goalMapper.goalToGoalResponseDto(goal);
  }

  @Transactional
  public void delete(Long goalId) {
    log.info("Deleting goal with id: {}", goalId);

    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(
                () -> {
                  log.error("Goal not found with id: {}", goalId);
                  return new EntityNotFoundException("Goal not found with id: " + goalId);
                });

    goalRepository.delete(goal);
    log.info("Goal with id: {} successfully deleted", goalId);

    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"goal", "goals"}, new Object[] {goalId, goal.getChild().getId()}));
  }

  @Cacheable(value = "goals", key = "#childId", unless = "#result.isEmpty()")
  public List<GoalResponseDto> findAll(Long childId) {
    log.info("Fetching all goals for child with id: {}", childId);

    List<GoalResponseDto> goals =
        goalMapper.goalToGoalResponseDtoList(goalRepository.findByChildId(childId));

    log.info("Found {} goals for child with id: {}", goals.size(), childId);

    return goals;
  }

  @Cacheable(value = "goal", key = "#goalId", unless = "#result == null")
  public GoalResponseDto findById(Long goalId) {
    log.info("Fetching goal with id: {}", goalId);

    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(
                () -> {
                  log.error("Goal not found with id: {}", goalId);
                  return new EntityNotFoundException("Goal not found with id: " + goalId);
                });

    log.info("Found goal with id: {}", goalId);
    return goalMapper.goalToGoalResponseDto(goal);
  }
}
