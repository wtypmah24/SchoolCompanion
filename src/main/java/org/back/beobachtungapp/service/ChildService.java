package org.back.beobachtungapp.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.mapper.ChildMapper;
import org.back.beobachtungapp.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    Child newChild = childMapper.childRequestDtoToChild(child);
    Companion companion = new Companion();
    companion.setId(companionDto.id());
    newChild.setSchoolCompanion(companion);
    Child savedChild = childRepository.save(newChild);
    return childMapper.childToChildResponseDto(savedChild);
  }

  @Cacheable(
      value = "children",
      key = "#companion.id",
      unless = "#result == null or #result.isEmpty()")
  public List<Child> findAllByCompanion(CompanionDto companion) {
    log.info("findAllByCompanion{}", companion);
    return childRepository.findAllBySchoolCompanionId(companion.id());
  }
}
