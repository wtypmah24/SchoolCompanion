package org.back.beobachtungapp.entity.note;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class Note {
  private long id;
  String content;
  private Long childId;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;
}
