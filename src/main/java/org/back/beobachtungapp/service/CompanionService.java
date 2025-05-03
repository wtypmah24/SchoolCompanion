package org.back.beobachtungapp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.mapper.CompanionMapper;
import org.back.beobachtungapp.repository.CompanionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    String encodedPassword = passwordEncoder.encode(companion.password());

    CompanionRequestDto newCompanion =
        new CompanionRequestDto(
            companion.name(), companion.surname(), companion.email(), encodedPassword);

    return companionRepository.save(companionMapper.companionRequestDtoToCompanion(newCompanion));
  }

  @Cacheable(value = "users", key = "#id", unless = "#result == null")
  public Companion findById(Long id) {
    return companionRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Companion not found with id: " + id));
  }

  @Cacheable(value = "users", key = "#email", unless = "#result == null")
  public Companion findCompanionByEmail(String email) {
    return companionRepository
        .findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Companion not found with email: " + email));
  }
}
