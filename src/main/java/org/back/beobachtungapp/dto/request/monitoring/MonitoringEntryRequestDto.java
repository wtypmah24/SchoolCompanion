package org.back.beobachtungapp.dto.request.monitoring;

import jakarta.validation.constraints.NotBlank;

public record MonitoringEntryRequestDto(
    @NotBlank(message = "Value cannot be blank") String value,

    // nullable field
    String notes) {}
