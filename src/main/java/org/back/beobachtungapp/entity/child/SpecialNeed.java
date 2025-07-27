package org.back.beobachtungapp.entity.child;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class SpecialNeed {
  private long id;
  private String type;
  private String description;
  private Long childId;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;
}
