package org.back.beobachtungapp.dto.response.companion;

import java.time.Instant;

public record CompanionDto(
    Long id,
    String name,
    String surname,
    String organization,
    String email,
    String tgId,
    String avatarId,
    String startWorkingTime,
    String endWorkingTime,
    Instant createdAt) {}
