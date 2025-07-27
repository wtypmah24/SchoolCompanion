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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildService {
  private final ChildDao childDao;

  @Transactional
  public void save(ChildRequestDto child, CompanionDto companionDto) {
    childDao.save(child, companionDto.id());
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public void update(ChildUpdateDto childUpdateDto, Long childId) {
    childDao.update(childUpdateDto, childId);
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public void delete(Long childId) {
    childDao.delete(childId);
  }

  public List<ChildResponseDto> findAll(CompanionDto companion) {
    return childDao.findAllBySchoolCompanionId(companion.id());
  }

  //  @Cacheable(value = "child", key = "#id", unless = "#result == null")
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
