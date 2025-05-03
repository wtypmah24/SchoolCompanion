package org.back.beobachtungapp.dto.response.companion;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;

@Schema(description = "Response payload for a companion object")
public record CompanionDto(
    @Schema(description = "Unique identifier of the companion", example = "1") Long id,
    @Schema(description = "First name of the companion", example = "John") String name,
    @Schema(description = "Surname of the companion", example = "Doe") String surname,
    @Schema(description = "Email address of the companion", example = "john.doe@example.com")
        String email)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
