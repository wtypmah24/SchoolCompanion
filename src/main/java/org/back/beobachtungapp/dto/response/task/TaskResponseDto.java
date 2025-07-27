package org.back.beobachtungapp.dto.response.task;

import java.time.Instant;

public record TaskResponseDto(
    Long id, String title, String description, String status, Instant deadLine) {}
