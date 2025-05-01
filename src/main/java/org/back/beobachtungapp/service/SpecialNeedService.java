package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.SpecialNeedRequestDto;
import org.back.beobachtungapp.dto.request.child.SpecialNeedUpdateDto;
import org.back.beobachtungapp.dto.response.child.SpecialNeedResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.child.SpecialNeed;
import org.back.beobachtungapp.event.CacheEvent;
import org.back.beobachtungapp.mapper.SpecialNeedMapper;
import org.back.beobachtungapp.repository.SpecialNeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SpecialNeedService {
  private final SpecialNeedRepository specialNeedRepo;
  private final SpecialNeedMapper specialNeedMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public SpecialNeedService(
      SpecialNeedRepository specialNeedRepo,
      SpecialNeedMapper specialNeedMapper,
      ApplicationEventPublisher eventPublisher) {
    this.specialNeedRepo = specialNeedRepo;
    this.specialNeedMapper = specialNeedMapper;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public SpecialNeedResponseDto save(SpecialNeedRequestDto goalDto, Long childId) {
    SpecialNeed need = specialNeedMapper.specialNeedRequestDtoToSpecialNeed(goalDto);

    Child childRef = new Child();
    childRef.setId(childId);
    need.setChild(childRef);

    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(specialNeedRepo.save(need));
  }

  @Transactional
  public SpecialNeedResponseDto update(SpecialNeedUpdateDto needUpdateDto, Long needId) {
    SpecialNeed need =
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> new EntityNotFoundException("SpecialNeed not found with id: " + needId));
    specialNeedMapper.updateSpecialNeedFromDto(needUpdateDto, need);
    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"need", "needs"}, new Object[] {needId, need.getChild().getId()}));

    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(need);
  }

  @Transactional
  public void delete(Long needId) {
    SpecialNeed need =
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> new EntityNotFoundException("SpecialNeed not found with id: " + needId));
    specialNeedRepo.delete(need);
    eventPublisher.publishEvent(
        new CacheEvent(
            new String[] {"need", "needs"}, new Object[] {needId, need.getChild().getId()}));
  }

  @Cacheable(value = "needs", key = "#childId")
  public List<SpecialNeedResponseDto> findAll(Long childId) {

    return specialNeedMapper.specialNeedToSpecialNeedResponseDtoList(
        specialNeedRepo.findByChildId(childId));
  }

  @Cacheable(value = "need", key = "#needId")
  public SpecialNeedResponseDto findById(Long needId) {
    return specialNeedMapper.specialNeedToSpecialNeedResponseDto(
        specialNeedRepo
            .findById(needId)
            .orElseThrow(
                () -> new EntityNotFoundException("SpecialNeed not found with id: " + needId)));
  }
}
