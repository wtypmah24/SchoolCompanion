package org.back.beobachtungapp.dto.request.monitoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MonitoringEntryRequestDto(

        @NotBlank(message = "Value cannot be blank")
        String value,

        @NotNull(message = "Date is required")
        LocalDate date,

        String notes,  // nullable field

        @NotNull(message = "Parameter id is required")
        Long monitoringParameterId

) {
}

