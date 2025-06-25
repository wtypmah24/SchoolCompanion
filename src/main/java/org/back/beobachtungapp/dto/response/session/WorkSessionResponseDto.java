package org.back.beobachtungapp.dto.response.session;

import java.time.Instant;

public record WorkSessionResponseDto(Long id, Instant startTime, Instant endTime, String note) {}
