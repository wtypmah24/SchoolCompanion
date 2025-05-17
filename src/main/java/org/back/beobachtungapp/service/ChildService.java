package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChildService {
  private final ChildMapper childMapper;
  private final ChildRepository childRepository;

  @Autowired
  public ChildService(ChildMapper childMapper, ChildRepository childRepository) {
    this.childMapper = childMapper;
    this.childRepository = childRepository;
  }

  @Transactional
  public ChildResponseDto save(ChildRequestDto child, CompanionDto companionDto) {
    log.info("Saving new child with data: {} and companion: {}", child, companionDto);
    Child newChild = childMapper.childRequestDtoToChild(child);
    Companion companion = new Companion();
    companion.setId(companionDto.id());
    newChild.setSchoolCompanion(companion);

    Child savedChild = childRepository.save(newChild);
    log.info("Successfully saved child with id: {}", savedChild.getId());

    return childMapper.childToChildResponseDto(savedChild);
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public ChildResponseDto update(
      ChildUpdateDto childUpdateDto, Long childId, CompanionDto companion) {
    log.info("Updating child with id: {} and companion: {}", childId, companion);

    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(
                () -> {
                  log.error("Child not found with id: {}", childId);
                  return new EntityNotFoundException("Child not found with id: " + childId);
                });

    childMapper.updateChildFromDto(childUpdateDto, child);
    log.info("Child with id: {} successfully updated", childId);
    return childMapper.childToChildResponseDto(child);
  }

  @CacheEvict(value = "child", key = "#childId")
  @Transactional
  public void delete(Long childId) {
    log.info("Deleting child with id: {}", childId);

    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(
                () -> {
                  log.error("Child not found with id: {}", childId);
                  return new EntityNotFoundException("Child not found: " + childId);
                });

    childRepository.delete(child);
    log.info("Child with id: {} successfully deleted", childId);
  }

  public List<ChildResponseDto> findAll(CompanionDto companion) {
    log.info("Fetching all children for companion with id: {}", companion.id());

    List<ChildResponseDto> children =
        childMapper.childToChildResponseDtoList(
            childRepository.findAllBySchoolCompanionId(companion.id()));

    log.info("Found {} children for companion with id: {}", children.size(), companion.id());

    return children;
  }

  @Cacheable(value = "child", key = "#id", unless = "#result == null")
  public ChildResponseDto findById(Long id) {
    log.info("Fetching child with id: {}", id);

    Child child =
        childRepository
            .findByIdCustom(id)
            .orElseThrow(
                () -> {
                  log.error("Child not found with id: {}", id);
                  return new EntityNotFoundException("Child not found with id: " + id);
                });

    log.info("Found child with id: {}", id);
    return childMapper.childToChildResponseDto(child);
  }

  public ChildWithAttachments getChildWithAttachments(Long childId) {
    Child child =
        childRepository
            .findByIdCustom(childId)
            .orElseThrow(
                () -> {
                  log.error("Child not found with id: {}", childId);
                  return new EntityNotFoundException("Child not found with id: " + childId);
                });

    log.info("Found child with id: {}", childId);
    return childMapper.childToChildWithAttachments(child);
  }
}
