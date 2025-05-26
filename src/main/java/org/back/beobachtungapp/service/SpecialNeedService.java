package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.back.beobachtungapp.mapper.SpecialNeedMapper;
import org.back.beobachtungapp.repository.SpecialNeedRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialNeedService {
  private final SpecialNeedRepository specialNeedRepo;
  private final SpecialNeedMapper specialNeedMapper;
  private final ChildService childService;

  @Transactional
  public SpecialNeedResponseDto save(SpecialNeedRequestDto goalDto, Long childId) {
    log.info("Saving special need for child with id: {}", childId);

    SpecialNeed need = specialNeedMapper.specialNeedRequestDtoToSpecialNeed(goalDto);
    Child child = childService.findChildOrThrow(childId);
    child.addSpecialNeed(need);
    SpecialNeed savedNeed = specialNeedRepo.save(need);
    log.info(
        "Successfully saved special need with id: {} for child with id: {}",
        savedNeed.getId(),
        childId);
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(savedNeed);
  }

  @CacheEvict(value = "need", key = "#needId")
  @Transactional
  public SpecialNeedResponseDto update(SpecialNeedUpdateDto needUpdateDto, Long needId) {
    SpecialNeed need = findNeedOrThrow(needId);
    specialNeedMapper.updateSpecialNeedFromDto(needUpdateDto, need);
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(need);
  }

  @CacheEvict(value = "need", key = "#needId")
  @Transactional
  public void delete(Long needId) {
    SpecialNeed need = findNeedOrThrow(needId);
    specialNeedRepo.delete(need);
  }

  public List<SpecialNeedResponseDto> findAll(Long childId) {
    return specialNeedMapper.specialNeedToSpecialNeedResponseDtoList(
        specialNeedRepo.findByChildId(childId));
  }

  @Cacheable(value = "need", key = "#needId", unless = "#result == null")
  public SpecialNeedResponseDto findById(Long needId) {
    SpecialNeed need = findNeedOrThrow(needId);
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(need);
  }

  private SpecialNeed findNeedOrThrow(Long needId) {
    return specialNeedRepo
        .findById(needId)
        .orElseThrow(
            () -> {
              log.error("SpecialNeed not found with id: {}", needId);
              return new EntityNotFoundException("SpecialNeed not found with id: " + needId);
            });
  }
}
