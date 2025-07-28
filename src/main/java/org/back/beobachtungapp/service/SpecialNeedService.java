package org.back.beobachtungapp.service;

import jakarta.ws.rs.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.SpecialNeedDao;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialNeedService {
  private final SpecialNeedDao needDao;

  @Transactional
  @CacheEvict(value = "needs", key = "#childId")
  public void save(SpecialNeedRequestDto needDto, Long childId) {
    needDao.save(needDto, childId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "need", key = "#needId"),
        @CacheEvict(value = "needs", key = "#childId")
      })
  @Transactional
  public void update(SpecialNeedUpdateDto needUpdateDto, Long needId, Long childId) {
    needDao.update(needUpdateDto, needId);
  }

  @Caching(
      evict = {
        @CacheEvict(value = "need", key = "#needId"),
        @CacheEvict(value = "needs", key = "#childId")
      })
  @Transactional
  public void delete(Long needId, Long childId) {
    needDao.delete(needId);
  }

  @Cacheable(value = "needs", key = "#childId", unless = "#result == null")
  public List<SpecialNeedResponseDto> findByChild(Long childId) {
    return needDao.findByChildId(childId);
  }

  @Cacheable(value = "need", key = "#needId", unless = "#result == null")
  public SpecialNeedResponseDto findById(Long needId) {
    return needDao
        .findById(needId)
        .orElseThrow(
            () -> {
              log.error("SpecialNeed not found with id: {}", needId);
              return new BadRequestException("SpecialNeed not found with id: " + needId);
            });
  }
}
