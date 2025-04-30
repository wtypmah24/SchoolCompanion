package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.request.child.ChildUpdateDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.event.ChildCacheEvictEvent;
import org.back.beobachtungapp.event.ChildCacheUpdateEvent;
import org.back.beobachtungapp.mapper.ChildMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChildService {
  private final ChildMapper childMapper;
  private final ChildRepository childRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public ChildService(
      ChildMapper childMapper,
      ChildRepository childRepository,
      ApplicationEventPublisher eventPublisher) {
    this.childMapper = childMapper;
    this.childRepository = childRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public ChildResponseDto save(ChildRequestDto child, CompanionDto companionDto) {
    Child newChild = childMapper.childRequestDtoToChild(child);
    Companion companion = new Companion();
    companion.setId(companionDto.id());
    newChild.setSchoolCompanion(companion);
    Child savedChild = childRepository.save(newChild);
    eventPublisher.publishEvent(new ChildCacheUpdateEvent(savedChild.getId(), companion.getId()));
    return childMapper.childToChildResponseDto(savedChild);
  }

  @Transactional
  public ChildResponseDto update(
      ChildUpdateDto childUpdateDto, Long childId, CompanionDto companion) {
    Child child =
        childRepository
            .findById(childId)
            .orElseThrow(
                () -> new EntityNotFoundException("Companion not found with id: " + childId));
    childMapper.updateChildFromDto(childUpdateDto, child);
    eventPublisher.publishEvent(
        new ChildCacheUpdateEvent(child.getId(), child.getSchoolCompanion().getId()));
    return childMapper.childToChildResponseDto(child);
  }

  @Transactional
  public void delete(Long childId) {
    Child child =
        childRepository
            .findById(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found: " + childId));

    Long companionId = child.getSchoolCompanion().getId();
    childRepository.delete(child);

    eventPublisher.publishEvent(new ChildCacheEvictEvent(childId, companionId));
  }

  @Cacheable(value = "children", key = "#companion.id()")
  public List<ChildResponseDto> findAll(CompanionDto companion) {
    log.info("findAllByCompanion{}", companion);
    return childMapper.childToChildResponseDtoList(
        childRepository.findAllBySchoolCompanionId(companion.id()));
  }

  @Cacheable(value = "child", key = "#id")
  public ChildResponseDto findById(Long id) {
    return childMapper.childToChildResponseDto(
        childRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + id)));
  }
}
