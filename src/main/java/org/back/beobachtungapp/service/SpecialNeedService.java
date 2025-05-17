package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.dto.update.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.back.beobachtungapp.mapper.SpecialNeedMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.SpecialNeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SpecialNeedService {
  private final SpecialNeedRepository specialNeedRepo;
  private final SpecialNeedMapper specialNeedMapper;
  private final ChildRepository childRepository;

  @Autowired
  public SpecialNeedService(
      SpecialNeedRepository specialNeedRepo,
      SpecialNeedMapper specialNeedMapper,
      ChildRepository childRepository) {
    this.specialNeedRepo = specialNeedRepo;
    this.specialNeedMapper = specialNeedMapper;
    this.childRepository = childRepository;
  }

  @Transactional
  public SpecialNeedResponseDto save(SpecialNeedRequestDto goalDto, Long childId) {
    log.info("Saving special need for child with id: {}", childId);

    SpecialNeed need = specialNeedMapper.specialNeedRequestDtoToSpecialNeed(goalDto);
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found"));
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
    log.info("Updating special need with id: {}", needId);

    SpecialNeed need =
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> {
                  log.error("SpecialNeed not found with id: {}", needId);
                  return new EntityNotFoundException("SpecialNeed not found with id: " + needId);
                });

    specialNeedMapper.updateSpecialNeedFromDto(needUpdateDto, need);
    log.info("Successfully updated special need with id: {}", needId);
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(need);
  }

  @CacheEvict(value = "need", key = "#needId")
  @Transactional
  public void delete(Long needId) {
    log.info("Deleting special need with id: {}", needId);

    SpecialNeed need =
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> {
                  log.error("SpecialNeed not found with id: {}", needId);
                  return new EntityNotFoundException("SpecialNeed not found with id: " + needId);
                });

    specialNeedRepo.delete(need);
    log.info("Successfully deleted special need with id: {}", needId);
  }

  public List<SpecialNeedResponseDto> findAll(Long childId) {
    log.info("Fetching all special needs for child with id: {}", childId);

    List<SpecialNeedResponseDto> specialNeeds =
        specialNeedMapper.specialNeedToSpecialNeedResponseDtoList(
            specialNeedRepo.findByChildId(childId));

    log.info("Found {} special needs for child with id: {}", specialNeeds.size(), childId);

    return specialNeeds;
  }

  @Cacheable(value = "need", key = "#needId", unless = "#result == null")
  public SpecialNeedResponseDto findById(Long needId) {
    log.info("Fetching special need with id: {}", needId);

    SpecialNeed need =
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> {
                  log.error("SpecialNeed not found with id: {}", needId);
                  return new EntityNotFoundException("SpecialNeed not found with id: " + needId);
                });

    log.info("Found special need with id: {}", needId);
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(need);
  }
}
