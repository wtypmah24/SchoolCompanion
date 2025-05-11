package org.back.beobachtungapp.dto.request.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "DTO for creating or updating a task")
public record TaskRequestDto(
    @Schema(description = "Task title", example = "Do homework")
        @NotBlank(message = "Title must not be blank")
        String title,
    @Schema(description = "Task description", example = "Complete math exercises")
        @NotBlank(message = "Description must not be blank")
        String description,
    @Schema(description = "Task status", example = "PENDING")
        @NotBlank(message = "Status is required")
        String status,
    @Schema(description = "Task deadline", example = "2025-06-01T18:00:00")
        @NotNull(message = "Deadline is required")
        LocalDateTime deadLine) {}
