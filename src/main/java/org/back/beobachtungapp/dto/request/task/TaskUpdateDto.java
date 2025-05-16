package org.back.beobachtungapp.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "DTO for creating or updating a task")
public record TaskUpdateDto(
    @Schema(description = "Task title", example = "Do homework") String title,
    @Schema(description = "Task description", example = "Complete math exercises")
        String description,
    @Schema(description = "Task status", example = "PENDING") String status,
    @Schema(description = "Task deadline", example = "2025-06-01T18:00:00") Instant deadLine) {}
