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

  @Autowired
  public GoalService(
      GoalRepository goalRepository,
      GoalMapper goalMapper,
      ApplicationEventPublisher eventPublisher) {
    this.goalRepository = goalRepository;
    this.goalMapper = goalMapper;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public GoalResponseDto save(GoalRequestDto goalDto, Long childId) {
    Goal goal = goalMapper.goalRequestDtoToGoal(goalDto);

    Child childRef = new Child();
    childRef.setId(childId);
    goal.setChild(childRef);

    return goalMapper.goalToGoalResponseDto(goalRepository.save(goal));
  }

  @Transactional
  public GoalResponseDto update(GoalRequestDto goalDto, Long goalId) {
    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));
    goalMapper.updateGoalFromDto(goalDto, goal);
    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"goal", "goals"}, new Object[] {goalId, goal.getChild().getId()}));

    return goalMapper.goalToGoalResponseDto(goal);
  }

  @Transactional
  public void delete(Long goalId) {
    Goal goal =
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));
    goalRepository.delete(goal);
    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"goal", "goals"}, new Object[] {goalId, goal.getChild().getId()}));
  }

  @Cacheable(value = "goals", key = "#childId")
  public List<GoalResponseDto> findAll(Long childId) {

    return goalMapper.goalToGoalResponseDtoList(goalRepository.findByChildId(childId));
  }

  @Cacheable(value = "goal", key = "#goalId")
  public GoalResponseDto findById(Long goalId) {
    return goalMapper.goalToGoalResponseDto(
        goalRepository
            .findById(goalId)
            .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId)));
  }
}
