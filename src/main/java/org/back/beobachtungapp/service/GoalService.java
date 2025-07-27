package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.GoalDao;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
  private final GoalDao goalDao;

  @Transactional
  public void save(GoalRequestDto goalDto, Long childId) {
    goalDao.save(goalDto, childId);
  }

  @Transactional
  @CacheEvict(value = "goals")
  public void update(GoalRequestDto goalDto, Long goalId) {
    goalDao.update(goalDto, goalId);
  }

  @CacheEvict(value = "goal")
  @Transactional
  public void delete(Long goalId) {
    goalDao.delete(goalId);
  }

  public List<GoalResponseDto> findByChild(Long childId) {
    return goalDao.findByChildId(childId);
  }

  @Cacheable(value = "goal", key = "goalId")
  public GoalResponseDto findById(Long goalId) {
    return goalDao
        .findById(goalId)
        .orElseThrow(
            () -> {
              log.error("Goal not found with id: {}", goalId);
              return new BadRequestException("Goal not found with id: " + goalId);
            });
  }
}
