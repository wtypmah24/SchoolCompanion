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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
  private final GoalDao goalDao;

  @Transactional
  @CacheEvict(value = "goals", key = "#childId")
  public void save(GoalRequestDto goalDto, Long childId) {
    goalDao.save(goalDto, childId);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = "goal", key = "#goalId"),
        @CacheEvict(value = "goals", key = "#childId")
      })
  public void update(GoalRequestDto goalDto, Long goalId, Long childId) {
    goalDao.update(goalDto, goalId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "goal", key = "#goalId"),
        @CacheEvict(value = "goals", key = "#childId")
      })
  @Transactional
  public void delete(Long goalId, Long childId) {
    goalDao.delete(goalId);
  }

  @Cacheable(value = "goals", key = "#childId", unless = "#result == null")
  public List<GoalResponseDto> findByChild(Long childId) {
    return goalDao.findByChildId(childId);
  }

  @Cacheable(value = "goal", key = "#goalId", unless = "#result == null")
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
