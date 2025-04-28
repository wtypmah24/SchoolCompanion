package org.back.beobachtungapp.dto.response.companion;

import java.io.Serial;
import java.io.Serializable;

public record CompanionDto(Long id, String name, String surname, String email)
    implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
}
