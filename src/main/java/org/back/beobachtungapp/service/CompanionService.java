package org.back.beobachtungapp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dao.CompanionDao;
import org.back.beobachtungapp.dto.request.companion.CompanionAdTgIdDto;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.response.companion.CompanionDto;
import org.back.beobachtungapp.dto.update.companion.CompanionUpdateDto;
import org.back.beobachtungapp.dto.update.companion.UpdatePasswordDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanionService {
  private final CompanionDao companionDao;
  private final PasswordEncoder passwordEncoder;

  @CacheEvict(value = "users")
  @Transactional
  public void save(CompanionRequestDto companion) {
    String encodedPassword = passwordEncoder.encode(companion.password());

    CompanionRequestDto newCompanion =
        new CompanionRequestDto(
            companion.name(),
            companion.surname(),
            companion.organization(),
            companion.email(),
            encodedPassword);

    companionDao.save(newCompanion);
  }

  @CacheEvict(value = "users")
  @Transactional
  public void update(CompanionUpdateDto dto, CompanionDto companionDto) {
    companionDao.update(dto, companionDto.id());
  }

  @Transactional
  public void updatePassword(UpdatePasswordDto dto, CompanionDto companionDto) {
    companionDao.updatePassword(
        companionDto.id(),
        passwordEncoder.encode(dto.newPassword()),
        passwordEncoder.encode(dto.currentPassword()));
  }

  @Transactional
  @CacheEvict(value = "users")
  public void delete(CompanionDto companionDto) {
    companionDao.delete(companionDto.id());
  }

  @Transactional
  public void addTgIdToCompanion(CompanionAdTgIdDto tgDto) {
    companionDao.addTgId(tgDto);
  }

  @Transactional
  public void addChatIdToCompanion(Long companionId, String newChatId) {
    companionDao.addThreadToCompanion(companionId, newChatId);
  }

  public List<String> getThreadIds(Long companionId) {
    return companionDao.getChatIds(companionId);
  }

  @Transactional
  public void deleteThreadIds(CompanionDto dto, String threadId) {
    companionDao.removeThread(dto.id(), threadId);
  }
}
