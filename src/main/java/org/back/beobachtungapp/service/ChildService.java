package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.child.ChildWithAttachments;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.child.ChildUpdateDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.mapper.ChildMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildService {
  private final ChildMapper childMapper;
  private final ChildRepository childRepository;
  private final CompanionRepository companionRepository;

  @Transactional
  public ChildResponseDto save(ChildRequestDto child, CompanionDto companionDto) {
    Child newChild = childMapper.childRequestDtoToChild(child);
    Companion companion = companionRepository.getReferenceById(companionDto.id());
    newChild.setSchoolCompanion(companion);

    Child savedChild = childRepository.save(newChild);

    return childMapper.childToChildResponseDto(savedChild);
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public ChildResponseDto update(ChildUpdateDto childUpdateDto, Long childId) {
    Child child = findChildOrThrow(childId);
    childMapper.updateChildFromDto(childUpdateDto, child);
    return childMapper.childToChildResponseDto(child);
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public void delete(Long childId) {
    Child child = findChildOrThrow(childId);
    childRepository.delete(child);
  }

  public List<ChildResponseDto> findAll(CompanionDto companion) {
    return childMapper.childToChildResponseDtoList(
        childRepository.findAllBySchoolCompanionId(companion.id()));
  }

  @Cacheable(value = "child", key = "#id", unless = "#result == null")
  public ChildResponseDto findById(Long id) {
    Child child = findChildOrThrow(id);
    return childMapper.childToChildResponseDto(child);
  }

  public ChildWithAttachments getChildWithAttachments(Long childId) {
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(() -> new EntityNotFoundException("Child not found with id: " + childId));
    return childMapper.childToChildWithAttachments(child);
  }

  protected Child findChildOrThrow(Long childId) {
    return childRepository
        .findById(childId)
        .orElseThrow(
            () -> {
              log.error("Child not found with id: {}", childId);
              return new EntityNotFoundException("Child not found with id: " + childId);
            });
  }
}
