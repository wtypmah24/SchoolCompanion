package org.back.beobachtungapp.dto.request.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record TaskRequestDto(
    @NotBlank(message = "Title must not be blank") String title,
    @NotBlank(message = "Description must not be blank")
        @Size(min = 5, max = 1000, message = "Description must be between 5 and 1000 characters")
        String description,
    @NotBlank(message = "Status is required") String status,
    @NotNull(message = "Deadline is required") Instant deadLine) {}
