package org.back.beobachtungapp.service;

import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.mapper.CompanionMapper;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CompanionService {
  private final CompanionRepository companionRepository;
  private final PasswordEncoder passwordEncoder;
  private final CompanionMapper companionMapper;

  @Autowired
  public CompanionService(
      CompanionRepository companionRepository,
      PasswordEncoder passwordEncoder,
      CompanionMapper companionMapper) {
    this.companionRepository = companionRepository;
    this.passwordEncoder = passwordEncoder;
    this.companionMapper = companionMapper;
  }

  @Transactional
  public Companion save(CompanionRequestDto companion) {
    log.info(
        "Saving new companion with name: {} and email: {}", companion.name(), companion.email());

    String encodedPassword = passwordEncoder.encode(companion.password());
    log.debug("Password for companion with email: {} encoded successfully", companion.email());

    CompanionRequestDto newCompanion =
        new CompanionRequestDto(
            companion.name(), companion.surname(), companion.email(), encodedPassword);

    Companion savedCompanion =
        companionRepository.save(companionMapper.companionRequestDtoToCompanion(newCompanion));
    log.info("Successfully saved new companion with id: {}", savedCompanion.getId());

    return savedCompanion;
  }
}
