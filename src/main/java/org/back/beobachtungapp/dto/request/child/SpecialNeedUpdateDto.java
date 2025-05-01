package org.back.beobachtungapp.dto.request.child;

import jakarta.validation.constraints.Size;

public record SpecialNeedUpdateDto(
    @Size(min = 2, max = 100, message = "Type must be between 2 and 100 characters") String type,
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
        String description) {}
