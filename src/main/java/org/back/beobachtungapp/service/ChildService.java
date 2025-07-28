package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.ChildDao;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.child.ChildUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildService {
  private final ChildDao childDao;

  @Transactional
  @CacheEvict(value = "children", key = "#companionDto.id()")
  public ChildResponseDto save(ChildRequestDto child, CompanionDto companionDto) {
    return childDao.save(child, companionDto.id());
  }

  @Caching(
      evict = {
        @CacheEvict(value = "child", key = "#childId"),
        @CacheEvict(value = "children", key = "#companionDto.id()")
      })
  @Transactional
  public void update(ChildUpdateDto childUpdateDto, Long childId, CompanionDto companionDto) {
    childDao.update(childUpdateDto, childId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "child", key = "#childId"),
        @CacheEvict(value = "children", key = "#companionDto.id()")
      })
  @Transactional
  public void delete(Long childId, CompanionDto companionDto) {
    childDao.delete(childId);
  }

  @Cacheable(value = "children", key = "#companion.id()", unless = "#result == null")
  public List<ChildResponseDto> findAll(CompanionDto companion) {
    return childDao.findAllBySchoolCompanionId(companion.id());
  }

  @Cacheable(value = "child", key = "#id", unless = "#result == null")
  public ChildResponseDto findById(Long id) {
    return childDao
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("Child not found with id: {}", id);
              return new BadRequestException("Child not found with id: " + id);
            });
  }
}
