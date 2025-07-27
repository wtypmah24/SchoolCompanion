package org.back.beobachtungapp.dto.update.task;

import java.time.Instant;

public record TaskUpdateDto(String title, String description, String status, Instant deadLine) {}
