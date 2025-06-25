package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.companion.CompanionAdTgIdDto;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.dto.update.companion.UpdatePasswordDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.mapper.CompanionMapper;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanionService {
  private final CompanionRepository companionRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompanionMapper companionMapper;

  @CacheEvict(value = "users")
  @Transactional
  public Companion save(CompanionRequestDto companion) {
    String encodedPassword = passwordEncoder.encode(companion.password());

    CompanionRequestDto newCompanion =
        new CompanionRequestDto(
            companion.name(),
            companion.surname(),
            companion.organization(),
            companion.email(),
            encodedPassword);

    return companionRepository.save(companionMapper.companionRequestDtoToCompanion(newCompanion));
  }

  @CacheEvict(value = "users")
  @Transactional
  public CompanionDto update(CompanionUpdateDto dto, CompanionDto companionDto) {
    Companion companion = findCompanionOrThrow(companionDto.id());
    companionMapper.updateCompanionFromDto(dto, companion);
    return companionMapper.companionToCompanionDto(companion);
  }

  @Transactional
  public void updatePassword(UpdatePasswordDto dto, CompanionDto companionDto) {
    Companion companion = findCompanionOrThrow(companionDto.id());
    String oldPassword = companion.getPassword();
    if (!dto.currentPassword().equals(oldPassword)) {
      throw new IllegalArgumentException("Old password is incorrect");
    }
    companion.setPassword(passwordEncoder.encode(dto.newPassword()));
    companionRepository.save(companion);
  }

  @Transactional
  @CacheEvict(value = "users")
  public void delete(CompanionDto companionDto) {
    companionRepository.deleteById(companionDto.id());
  }

  @Transactional
  public void addTgIdToCompanion(CompanionAdTgIdDto tgDto) {
    Companion companion =
        companionRepository
            .findByEmail(tgDto.email())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Companion not found with email: " + tgDto.email()));

    companion.setTgId(tgDto.tgId());
    companionRepository.save(companion);
  }

  @Transactional
  public void addChatIdToCompanion(Long companionId, String newChatId) {
    Companion companion = findCompanionOrThrow(companionId);
    companion.getChatIds().add(newChatId);
    companionRepository.save(companion);
  }

  public Set<String> getThreadIds(Long companionId) {
    Companion companion = findCompanionOrThrow(companionId);
    return companion.getChatIds();
  }

  @Transactional
  public void deleteThreadIds(CompanionDto dto, String threadId) {
    companionRepository.removeThread(dto.id(), threadId);
  }

  private Companion findCompanionOrThrow(Long id) {
    return companionRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Companion not found with id: " + id));
  }
}
