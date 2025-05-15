package org.back.beobachtungapp.dto.response.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "DTO for representing task data in responses")
public record TaskResponseDto(
    @Schema(description = "Unique identifier of the task", example = "1") Long id,
    @Schema(description = "Task title", example = "Do homework") String title,
    @Schema(description = "Task description", example = "Complete math exercises")
        String description,
    @Schema(description = "Task status", example = "PENDING") String status,
    @JsonDeserialize(using = InstantDeserializer.class)
        @JsonSerialize(using = InstantSerializer.class)
        @Schema(description = "Task deadline", example = "2025-06-01T18:00:00")
        Instant deadLine) {}
