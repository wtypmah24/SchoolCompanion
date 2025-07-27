package org.back.beobachtungapp.entity.child;

import java.time.Instant;
import lombok.Data;

@Data
public class Goal {
  private long id;
  private String description;
  private Long childId;
  private Instant updatedAt;
  Instant createdAt;
}
