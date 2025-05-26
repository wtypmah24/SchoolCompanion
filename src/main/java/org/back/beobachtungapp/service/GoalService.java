package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.child.Goal;
import org.back.beobachtungapp.mapper.GoalMapper;
import org.back.beobachtungapp.repository.GoalRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
  private final GoalRepository goalRepository;
  private final GoalMapper goalMapper;
  private final ChildService childService;

  @Transactional
  public GoalResponseDto save(GoalRequestDto goalDto, Long childId) {
    Goal goal = goalMapper.goalRequestDtoToGoal(goalDto);
    Child child = childService.findChildOrThrow(childId);
    child.addGoal(goal);
    Goal savedGoal = goalRepository.save(goal);
    return goalMapper.goalToGoalResponseDto(savedGoal);
  }

  @Transactional
  @CacheEvict(value = "goals")
  public GoalResponseDto update(GoalRequestDto goalDto, Long goalId) {
    Goal goal = findGoalOrThrow(goalId);
    goalMapper.updateGoalFromDto(goalDto, goal);
    return goalMapper.goalToGoalResponseDto(goal);
  }

  @CacheEvict(value = "goal")
  @Transactional
  public void delete(Long goalId) {
    Goal goal = findGoalOrThrow(goalId);
    goalRepository.delete(goal);
  }

  @Transactional(readOnly = true)
  public List<GoalResponseDto> findAll(Long childId) {
    return goalMapper.goalToGoalResponseDtoList(goalRepository.findByChildId(childId));
  }

  @Cacheable(value = "goal", key = "goalId")
  public GoalResponseDto findById(Long goalId) {
    Goal goal = findGoalOrThrow(goalId);
    return goalMapper.goalToGoalResponseDto(goal);
  }

  private Goal findGoalOrThrow(Long goalId) {
    return goalRepository
        .findById(goalId)
        .orElseThrow(
            () -> {
              log.error("Goal not found with id: {}", goalId);
              return new EntityNotFoundException("Goal not found with id: " + goalId);
            });
  }
}
