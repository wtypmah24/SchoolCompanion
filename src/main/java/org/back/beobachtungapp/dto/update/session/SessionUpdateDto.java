package org.back.beobachtungapp.dto.update.session;

import java.time.LocalDateTime;

public record SessionUpdateDto(LocalDateTime startTime, LocalDateTime endTime, String note) {}
