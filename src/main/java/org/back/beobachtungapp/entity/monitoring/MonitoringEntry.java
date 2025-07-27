package org.back.beobachtungapp.entity.monitoring;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public class MonitoringEntry {
  private long id;
  private String value;
  private String notes;
  private Long monitoringParameterId;
  private Long childId;
  @LastModifiedDate Instant updatedAt;
  @CreatedDate() Instant createdAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MonitoringEntry that)) return false;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }
}
