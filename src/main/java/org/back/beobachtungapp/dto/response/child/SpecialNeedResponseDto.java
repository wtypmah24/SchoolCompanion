package org.back.beobachtungapp.dto.response.child;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload for a special need")
public record SpecialNeedResponseDto(
    @Schema(description = "Id of the special need", example = "1L") Long id,
    @Schema(description = "Type of the special need", example = "Mobility support") String type,
    @Schema(
            description = "Description of the special need",
            example = "Requires wheelchair access and ramp availability")
        String description) {}
