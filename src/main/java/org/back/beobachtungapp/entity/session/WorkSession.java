package org.back.beobachtungapp.entity.session;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class WorkSession {
  private Long id;
  private Instant startTime;
  private Instant endTime;
  private String note;
  private Long companionId;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;

  public static WorkSession copyOf(WorkSession other) {
    WorkSession copy = new WorkSession();
    copy.id = other.id;
    copy.startTime = other.startTime;
    copy.endTime = other.endTime;
    copy.note = other.note;
    copy.companionId = other.companionId;
    copy.createdAt = other.createdAt;
    copy.updatedAt = other.updatedAt;
    return copy;
  }
}
