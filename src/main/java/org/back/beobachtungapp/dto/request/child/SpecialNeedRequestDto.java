package org.back.beobachtungapp.dto.request.child;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SpecialNeedRequestDto(
    @NotBlank(message = "Type cannot be blank")
        @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters")
        String type,
    @NotBlank(message = "Description cannot be blank")
        @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description

    // We don't include entityId here, if we send child id in URL:
    // for example: POST /children/{id}/special-needs
    ) {}
