package org.back.beobachtungapp.entity.task;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class Task {
  private Long id;
  private String title;
  private String description;
  private String status;
  private Long childId;
  private Long companionId;
  private Instant deadLine;
  @CreatedDate() private Instant createdAt;
  @LastModifiedDate private Instant updatedAt;
}
