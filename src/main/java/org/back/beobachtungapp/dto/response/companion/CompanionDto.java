package org.back.beobachtungapp.dto.response.companion;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;

@Schema(description = "Response payload for a companion object")
public record CompanionDto(
    @Schema(description = "Unique identifier of the companion", example = "1") Long id,
    @Schema(description = "First name of the companion", example = "John") String name,
    @Schema(description = "Surname of the companion", example = "Doe") String surname,
    @Schema(description = "Organization of the companion", example = "Good Company GmbH")
        String organization,
    @Schema(description = "Email address of the companion", example = "john.doe@example.com")
        String email,
    @Schema(description = "Telegram id of the companion", example = "4354654464") String tgId,
    @Schema(description = "Date & time when acc was created", example = "04-05-2025 10:00:00 02:00")
        Instant createdAt)
    implements Serializable {}
